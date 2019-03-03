package io.github.pauljamescleary.petstore.client.components

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import io.github.pauljamescleary.petstore.domain
import io.github.pauljamescleary.petstore.client
import domain.pets.Pet
import domain.pets.PetStatus.{Adopted, Available, Pending}
import client.logger._
import client.css.GlobalStyles
import client.services.{DeletePet, PetsData, RefreshPets, UpsertPet}
import io.github.pauljamescleary.petstore.client.bootstrap._
import io.github.pauljamescleary.petstore.client.img.FontAwesomeTags
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._

object Pets {

  case class Props(proxy: ModelProxy[Pot[PetsData]])

  case class State(selectedItem: Option[Pet] = None, showPetForm: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
    // dispatch a message to refresh the todos, which will cause TodoStore to fetch todos from the server
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshPets))

    def editPet(item: Option[Pet]) =
    // activate the edit dialog
      $.modState(s => s.copy(selectedItem = item, showPetForm = true))

    def petEdited(item: Pet, cancelled: Boolean) = {
      val cb = if (cancelled) {
        // nothing to do here
        Callback.log("Pet editing cancelled")
      } else {
        Callback.log(s"Pet edited: $item") >>
          $.props >>= (_.proxy.dispatchCB(UpsertPet(item)))
      }
      // hide the edit dialog, chain callbacks
      cb >> $.modState(s => s.copy(showPetForm = false))
    }

    def render(p: Props, s: State) =
      Card()(
        CardHeader()("Pets in the Kennel"),
        CardBody()(
          <.div(
            p.proxy().renderFailed(ex => "Error loading"),
            p.proxy().renderPending(_ > 500, _ => "Loading..."),
            p.proxy().render(pd =>
              PetList(
                pd.pets,
                item => p.proxy.dispatchCB(UpsertPet(item)),
                item => editPet(Some(item)),
                item => p.proxy.dispatchCB(DeletePet(item))
              )
            ),
            Button(onClick = editPet(None).toJsCallback)(FontAwesomeTags.plusSquare, " New"),
            // if the dialog is open, add it to the panel
            if (s.showPetForm)
              PetForm(PetForm.Props(s.selectedItem, petEdited))
            else // otherwise add an empty placeholder
              VdomArray.empty()
          )
        )
      )
  }

  // create the React component for To Do management
  val component = ScalaComponent.builder[Props]("Pets")
    .initialState(State()) // initial state from TodoStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[Pot[PetsData]]) = component(Props(proxy))
}

object PetForm {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(item: Option[Pet], submitHandler: (Pet, Boolean) => Callback)

  case class State(pet: Pet, cancelled: Boolean = true, show: Boolean = true)

  class Backend($: BackendScope[Props, State]) {

    def cancel(): Callback = {
      $.modState(s => s.copy(cancelled = true, show = false))
    }

    def save(): Callback = {
      $.modState(s => s.copy(cancelled = false, show = false))
    }

    def onExit(state: State, props: Props): Callback = {
      props.submitHandler(state.pet, state.cancelled)
    }

    def updateBio(e: ReactEventFromInput) = {
      val text = e.target.value
      // update TodoItem content
      $.modState(s => s.copy(pet = s.pet.copy(bio = text)))
    }

    def updateStatus(e: ReactEventFromInput) = {
      // update TodoItem priority
      val newStatus = e.currentTarget.value match {
        case p if p == Available.toString => Available
        case p if p == Pending.toString => Pending
        case p if p == Adopted.toString => Adopted
      }
      $.modState(s => s.copy(pet = s.pet.copy(status = newStatus)))
    }

    def render(p: Props, s: State) = {
      log.debug(s"User is ${if (s.pet.id.isEmpty) "adding" else "editing"} a pet or two")
      val headerText = if (s.pet.id.isEmpty) "Add new pet" else "Edit pet"
      Modal(
        show = s.show,
        onExit = onExit(s,p).toJsCallback,
        onHide = onExit(s,p).toJsCallback      // to handle clicking outside the modal
      )(
        ModalHeader()(
          ModalTitle()(headerText)
        ),
        ModalFooter()(
          Button(varient = "secondary", onClick = cancel().toJsCallback)("Close"),
          Button(varient = "primary", onClick = save().toJsCallback)("Save Changes")
        ),
        ModalBody()(
          <.div(bss.formGroup,
            <.label(^.`for` := "bio", "Biography"),
            <.input.text(bss.formControl, ^.id := "bio", ^.value := s.pet.bio,
              ^.placeholder := "write biography", ^.onChange ==> updateBio)),
          <.div(bss.formGroup,
            <.label(^.`for` := "status", "Status"),
            // using defaultValue = "Normal" instead of option/selected due to React
            <.select(bss.formControl, ^.id := "status", ^.value := s.pet.status.toString, ^.onChange ==> updateStatus,
              <.option(^.value := Available.toString, "Available"),
              <.option(^.value := Pending.toString, "Pending"),
              <.option(^.value := Adopted.toString, "Adopted")
            )
          )
        )
      )
    }
  }

  val component = ScalaComponent.builder[Props]("PetForm")
    .initialStateFromProps(p => State(p.item.getOrElse(Pet("", "", ""))))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}