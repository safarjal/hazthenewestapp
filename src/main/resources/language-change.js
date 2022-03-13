const onChangeFunction = (e) => {
  const languageSelector = e
  const englishElements = document.getElementsByClassName("english")
  const urduElements = document.getElementsByClassName("urdu")

  for (let i = 0; i < englishElements.length; i++) englishElements[i].classList.toggle("lang-invisible", languageSelector.value == "urdu")
  for (let i = 0; i < urduElements.length; i++) urduElements[i].classList.toggle("lang-invisible", languageSelector.value == "english")
  document.body.classList.toggle("rtl", languageSelector.value == "urdu")
}

const onClickFunction = (lang) => {
  const englishElements = document.getElementsByClassName("english-switch")
  const urduElements = document.getElementsByClassName("urdu-switch")
  const englishTab = document.getElementById("english-tab")
  const urduTab = document.getElementById("urdu-tab")

  englishTab.classList.toggle("selected", lang == "english")
  urduTab.classList.toggle("selected", lang == "urdu")
  for (let i = 0; i < englishElements.length; i++) englishElements[i].classList.toggle("invisible", lang == "urdu")
  for (let i = 0; i < urduElements.length; i++) urduElements[i].classList.toggle("invisible", lang == "english")
}

function getUrlVars() {
  var vars = {};
  window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
      vars[key] = value;
  });
  return vars;
}

const onLoad = () => {
  const lang = getUrlVars()["lang"]
  if (lang == "en") {
    const languageSelector = document.getElementById("language")
    languageSelector.value = "english"
    onChangeFunction(languageSelector)
  }
}

window.onload = onLoad