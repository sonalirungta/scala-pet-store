package io.github.pauljamescleary.petstore.frontend.components

import io.github.pauljamescleary.petstore.frontend._
import AppRouter.{AppPage, HomePageRt}
import models.Menu
import css.CssSettings._
import scalacss.ScalaCssReact._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Reusability
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._

object TopNav {

  import io.github.pauljamescleary.petstore.frontend.css.GlobalStyles._

  object Style extends StyleSheet.Inline {

    import dsl._

    val navMenu = style(display.flex,
      alignItems.center,
      backgroundColor(c"#F2706D"),
      margin.`0`,
      listStyle := "none")

    val menuItem = styleF.bool { selected =>
      styleS(
        //padding(20.px),
        fontSize(1.5.em),
        cursor.pointer,
        color(c"rgb(244, 233, 233)")
        //,mixinIfElse(selected)(backgroundColor(c"#E8433F"), fontWeight._500)(
        //  &.hover(backgroundColor(c"#B6413E")))
      )
    }
  }

  case class Props(menus: Vector[Menu],
                   selectedPage: AppPage,
                   ctrl: RouterCtl[AppPage])

  implicit val currentPageReuse = Reusability.by_==[AppPage]
  implicit val propsReuse = Reusability.by((_: Props).selectedPage)

  val component = ScalaComponent
      .builder[Props]("TopNav")
      .render_P { P =>
        <.header(
          <.nav(bootstrapStyles.navbar,
            <.div(^.`class` := "container-fluid",
              <.div(^.`class` := "navbar-header",
                <.a(^.`class` := "navbar-brand", P.ctrl setOnClick HomePageRt)("Pet Store")
              ),
            <.ul.apply(
              //Style.navMenu,
              ^.`class` := "nav navbar-nav",
              P.menus.toTagMod { item =>
                <.li(
                  ^.key := item.name,
                  Style.menuItem(item.route.getClass == P.selectedPage.getClass),
                  <.a(^.`class` := "navbar-brand", P.ctrl setOnClick item.route)(item.name)
                  //,item.name
                  //,P.ctrl setOnClick item.route
                )
              }
            )
            )
          )
        )
      }
      .configure(Reusability.shouldComponentUpdate)
      .build

  def apply(props: Props) = component(props)

}