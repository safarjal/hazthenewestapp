import kotlinx.browser.document
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

@OptIn(DelicateCoroutinesApi::class)
fun profilePage() {
    logoutDiv.innerHTML = ""
    logoutDiv.addHazappButton()
    rootHazapp!!.innerHTML = ""
    rootHazapp.appendChild {
        div {
            id = Ids.InputContainers.INPUT_CONTAINERS_CONTAINER
            div(classes = Ids.InputContainers.INPUT_CONTAINER) {
                id = Ids.InputContainers.INPUT_CONTAINER
                form(action = "javascript:void(0);") {
                    div(classes = CssC.ROW) {
                        button(classes = CssC.CALC_BTN) {
                            name = Ids.LoginLogout.LOGOUT_BUTTON
                            id = Ids.LoginLogout.LOGOUT_BUTTON
                            onClickFunction = { logout() }
                            makeSpans(Strings::logout)
                        }
                    }
                    div(classes = CssC.ROW) {
                        makeSpans(Strings::displayname)
                        input {
                            name = Ids.ProfilePage.DISPLAY_NAME
                            id = Ids.ProfilePage.DISPLAY_NAME
                        }
                        if (userId.isNullOrEmpty()) {
                            makeSpans(
                                "Please Log In again to use this feature",
                                "Please Log In again to use this feature",
                                "Please Log In again to use this feature"
                            )
                        }
                    }
                    div(classes = CssC.ROW) {
                        submitInput(classes = CssC.CALC_BTN) {
                            name = Ids.ProfilePage.CHANGE_BUTTON
                            id = Ids.ProfilePage.CHANGE_BUTTON
//                            TODO:
                            value = englishStrings.submit
                            onClickFunction = {
                                val displayName = findInputContainer(it).displayName
                                GlobalScope.launch { changeName(displayName) }
                            }
                        }
                    }
                    content(Ids.ERROR_MESSAGE)
                }
            }
        }
    }
}

private val HTMLElement.displayName get() = (getChildById(Ids.ProfilePage.DISPLAY_NAME) as HTMLInputElement).value
