import io.ktor.http.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import kotlin.js.Json

fun Node.loginPage() {
    append {
        div {
            id = Ids.InputContainers.INPUT_CONTAINERS_CONTAINER
            div(classes = Ids.InputContainers.INPUT_CONTAINER) {
                id = Ids.InputContainers.INPUT_CONTAINER
                form(action = "javascript:void(0);") {
                    div(classes = CssC.ROW) {
                        makeSpans("Username", "Username")
                        input {
                            name = Ids.Login.USERNAME
                            id = Ids.Login.USERNAME
                            placeholder = Ids.Login.USERNAME
                        }
                    }
                    div(classes = CssC.ROW) {
                        makeSpans("Password", "Password")
                        passwordInput {
                            name = Ids.Login.PASSWORD
                            id = Ids.Login.PASSWORD
                        }
                    }
                    div(classes = CssC.ROW) {
                        submitInput {
                            name = "submit"
                            id = "submit"
                            value = "submit"
                            onClickFunction = {
                                val username = findInputContainer(it).username
                                val password = findInputContainer(it).password
                                console.log(username, password)
                                var response: Pair<HttpStatusCode, Unit> =
                                    Pair(HttpStatusCode.NoContent, JSON.parse("null"))
//                                var response: Headers
                                GlobalScope.launch { response = login(username, password) }.invokeOnCompletion {
                                    console.log("yes", response.first.value == HttpStatusCode.OK.value)
                                    if (response.first.value == 200) {
                                        kotlinx.browser.sessionStorage.setItem("loggedIn", true.toString())
                                        rootHazapp.first().innerHTML = ""
                                        hazappPage()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private val HTMLElement.username get() = (getChildById(Ids.Login.USERNAME) as HTMLInputElement).value
private val HTMLElement.password get() = (getChildById(Ids.Login.PASSWORD) as HTMLInputElement).value
