package io.github.pauljamescleary.petstore.frontend.pages

import diode.react.ReactPot._
import diode.data.Pot
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^.{^, _}
import io.github.pauljamescleary.petstore.frontend._
import AppRouter.AppPage
import io.github.pauljamescleary.petstore.frontend.css.Bootstrap.Panel
import io.github.pauljamescleary.petstore.frontend.css.GlobalStyles
import io.github.pauljamescleary.petstore.frontend.services.SignIn
//import components._
import io.github.pauljamescleary.petstore.frontend.services.UserProfile

import scala.language.existentials

object SignInPage {

  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[AppPage], proxy: ModelProxy[Pot[UserProfile]])

  case class State(username: String, password: String)

  import css.CssSettings._
  import scalacss.ScalaCssReact._

  object Style extends StyleSheet.Inline {
    import dsl._

    val content = style(textAlign.center,
      fontSize(30.px),
      minHeight(450.px),
      paddingTop(40.px))
  }

  // create the React component for Dashboard
  private val component = ScalaComponent.builder[Props]("SignIn")
      // create and store the connect proxy in state for later use
      .initialStateFromProps(props => State("", ""))
      .renderPS { (b, p, s) =>
        <.div(
          Panel(
            Panel.Props("Sign In"),
            <.h2("Hello")
          ),
          <.form(^.onSubmit ==> {ev => p.proxy.dispatchCB(SignIn(s.username, s.password))},
            <.div(bss.formGroup,
              <.label(^.`for` := "description", "Username"),
              <.input.text(bss.formControl,
                ^.id := "username",
                ^.value := s.username,
                ^.placeholder := "Username",
                ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; b.modState(_.copy(username = text))}
              )
            ),
            <.div(bss.formGroup,
              <.label(^.`for` := "description", "Password"),
              <.input.text(bss.formControl,
                ^.id := "password",
                ^.value := s.password,
                ^.placeholder := "Password",
                ^.onChange ==> {ev: ReactEventFromInput => val text = ev.target.value; b.modState(_.copy(password = text))}
              )
            ),
            <.button("Submit")
          )
        )
      }
      .build

  def apply(router: RouterCtl[AppPage], proxy: ModelProxy[Pot[UserProfile]]) = component(Props(router, proxy))
}
