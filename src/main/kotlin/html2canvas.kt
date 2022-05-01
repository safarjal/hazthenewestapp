import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import kotlin.js.Promise

@JsModule("html2canvas")
@JsNonModule
external fun html2canvas(element: HTMLElement, vararg options: Any): Promise<HTMLCanvasElement>
