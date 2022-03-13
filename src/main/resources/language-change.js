const onChangeFunction = (e) => {
  const englishElements = document.getElementsByClassName("english")
  const urduElements = document.getElementsByClassName("urdu")
  const englishTab = document.getElementById("english-tab")
  const urduTab = document.getElementById("urdu-tab")
  const leftToRight = document.querySelector(".side")

  for (let i = 0; i < englishElements.length; i++) englishElements[i].classList.toggle("lang-invisible", e == "urdu")
  for (let i = 0; i < urduElements.length; i++) urduElements[i].classList.toggle("lang-invisible", e == "english")
  document.body.classList.toggle("rtl", e == "urdu")

  if (englishTab) englishTab.classList.toggle("selected", e == "english")
  if (englishTab && urduTab) urduTab.classList.toggle("selected", e == "urdu")
  if (leftToRight) leftToRight.classList.toggle("right", e == "english")
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
    onChangeFunction("english")
  }
}

window.onload = onLoad