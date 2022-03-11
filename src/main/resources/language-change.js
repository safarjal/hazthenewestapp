const onChangeFunction = (e) => {
  const languageSelector = e
  const englishElements = document.getElementsByClassName("english")
  const urduElements = document.getElementsByClassName("urdu")

  console.log("englishElements", englishElements)
  for (let i = 0; i < englishElements.length; i++) {englishElements[i].classList.toggle("lang-invisible", languageSelector.value == "urdu")}
  for (let i = 0; i < urduElements.length; i++) {urduElements[i].classList.toggle("lang-invisible", languageSelector.value == "english")}
  document.body.classList.toggle("rtl", languageSelector.value == "urdu")
}