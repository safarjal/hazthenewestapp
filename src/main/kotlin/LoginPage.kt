import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.w3c.dom.*

@OptIn(DelicateCoroutinesApi::class)
fun Node.loginPage() {
    logoutDiv.innerHTML = ""
    appendChild {
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
                        submitInput {
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
                    p {id = "errorMessage"}
                }
            }
        }
    }
}

public val HTMLElement.errorMessage get() = getChildById("errorMessage") as HTMLParagraphElement
private val HTMLElement.username get() = (getChildById(Ids.LoginLogout.USERNAME) as HTMLInputElement).value
private val HTMLElement.password get() = (getChildById(Ids.LoginLogout.PASSWORD) as HTMLInputElement).value

fun Node.addLogoutButton() {
    appendChild {
        button {
            name = Ids.LoginLogout.LOGOUT_BUTTON
            id = Ids.LoginLogout.LOGOUT_BUTTON
//            TODO:
            onClickFunction = { logout() }
            makeSpans(Strings::logout)
        }
    }
}

fun Node.addLoginButton() {
    appendChild {
        button {
            name = "login_button"
            id = "login_button"
//            TODO:
            onClickFunction = {
                rootHazapp!!.innerHTML = ""
                rootHazapp.loginPage()
            }
            makeSpans("Login", "Login")
        }
    }
}
