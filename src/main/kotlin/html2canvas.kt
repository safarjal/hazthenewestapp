import kotlinx.coroutines.await
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import kotlin.js.Promise

suspend fun HTMLElement.toCanvas(
    ignoreElements: (element: Element) -> Boolean = { false },
    onclone: (document: Document, element: HTMLElement) -> Unit = { _, _ -> },
    allowTaint: Boolean = false,
    scrollX: Double? = null,
    scrollY: Double? = null,
    windowWidth: Int? = null,
    windowHeight: Int? = null,
    scale: Double? = null,
    canvas: HTMLCanvasElement? = null,
    x: Int = 0,
    y: Int = 0,
    width: Int? = null,
    height: Int? = null,
    logging: Boolean = true,
    imageTimeout: Int = 15000,
    useCORS: Boolean = false,
    proxy: String? = null,
    backgroundColor: String? = undefined,
    foreignObjectRendering: Boolean = false,
    removeContainer: Boolean = true,
) = html2canvas(this, Html2CanvasOptions(
    ignoreElements, onclone, allowTaint,
    scrollX, scrollY, windowWidth, windowHeight,
    scale, canvas, x, y, width, height,
    logging,
    imageTimeout, useCORS, proxy,
    backgroundColor, foreignObjectRendering, removeContainer,
)).await()

@JsModule("html2canvas")
@JsNonModule
external fun html2canvas(
    element: HTMLElement,
    options: Html2CanvasOptions = definedExternally
): Promise<HTMLCanvasElement>

@OptIn(ExperimentalJsExport::class)
@JsExport
@Suppress("NON_EXPORTABLE_TYPE")
data class Html2CanvasOptions(
    // clone options
    val ignoreElements: (element: Element) -> Boolean = { false },
    val onclone: (document: Document, element: HTMLElement) -> Unit = { _, _ -> },
    val allowTaint: Boolean = false,
    // window options
    val scrollX: Double? = null,
    val scrollY: Double? = null,
    val windowWidth: Int? = null,
    val windowHeight: Int? = null,
    // render options
    val scale: Double? = null,
    val canvas: HTMLCanvasElement? = null,
    val x: Int = 0,
    val y: Int = 0,
    val width: Int? = null,
    val height: Int? = null,
    // context options
    val logging: Boolean = true,
    // resource options
    val imageTimeout: Int = 15000,
    val useCORS: Boolean = false,
    val proxy: String? = null,
    // more options
    val backgroundColor: String? = undefined,
    val foreignObjectRendering: Boolean = false,
    val removeContainer: Boolean = true,
)
