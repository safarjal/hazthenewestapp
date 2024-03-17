import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.Node

@OptIn(DelicateCoroutinesApi::class)
fun loginPage() {
    logoutDiv.innerHTML = ""
    rootHazapp!!.innerHTML = ""
    rootHazapp.appendChild {
        div {
            id = Ids.InputContainers.INPUT_CONTAINERS_CONTAINER
            div(classes = Ids.InputContainers.INPUT_CONTAINER) {
                id = Ids.InputContainers.INPUT_CONTAINER
                form(action = "javascript:void(0);") {
                    div(classes = CssC.ROW) {
                        makeSpans(Strings::username)
                        input {
                            name = Ids.LoginLogout.USERNAME
                            id = Ids.LoginLogout.USERNAME
                            placeholder = Ids.LoginLogout.USERNAME
                        }
                    }
                    div(classes = CssC.ROW) {
                        makeSpans(Strings::password)
                        passwordInput {
                            name = Ids.LoginLogout.PASSWORD
                            id = Ids.LoginLogout.PASSWORD
                        }
                    }
                    div(classes = CssC.ROW) {
                        submitInput(classes = CssC.CALC_BTN) {
                            name = Ids.LoginLogout.SUBMIT
                            id = Ids.LoginLogout.SUBMIT
//                            TODO:
                            value = englishStrings.submit
                            onClickFunction = {
                                val username = findInputContainer(it).username
                                val password = findInputContainer(it).password
                                GlobalScope.launch { login(username, password) }
                            }
                        }
                    }
                    content(Ids.ERROR_MESSAGE)
                }
            }
        }
    }
}

val HTMLElement.errorMessage get() = getChildById(Ids.ERROR_MESSAGE) as HTMLParagraphElement
private val HTMLElement.username get() = (getChildById(Ids.LoginLogout.USERNAME) as HTMLInputElement).value
private val HTMLElement.password get() = (getChildById(Ids.LoginLogout.PASSWORD) as HTMLInputElement).value

fun Node.addProfileButton() {
    appendChild {
        button(classes = "nav-link") {
            name = Ids.LoginLogout.LOGOUT_BUTTON
            id = Ids.LoginLogout.LOGOUT_BUTTON
//            TODO:
            onClickFunction = { profilePage() }
            img(classes = "icon") {
                src = "./images/profile-icon.svg"
                alt = "Profile"
            }
        }
    }
}

fun Node.addHazappButton() {
    appendChild {
        button(classes = CssC.CALC_BTN) {
            name = Ids.LoginLogout.LOGOUT_BUTTON
            id = Ids.LoginLogout.LOGOUT_BUTTON
//            TODO:
            onClickFunction = { hazappPage() }
            makeSpans(Strings::goBack)
        }
    }
}