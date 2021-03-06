const cardTemplate = document.querySelector("[data-template]")
const cardContainer = document.querySelector("[data-cards-container]")
const searchInput = document.querySelector("[data-search]")

let words = []

searchInput.addEventListener("input", (e) => {
       const value = e.target.value.toLowerCase();
       const isUsing = value.length > 0;
       words.forEach(word => {
            const isVisible = word.word.toLowerCase().includes(value);
            word.element.classList.toggle("hide", !(isVisible&&isUsing));
            
       })
})

//fetch("https://jsonplaceholder.typicode.com/users")
fetch("/SoT/searchbar/words.json")
.then(response => response.json())
.then(data => {
    words = data.map(user=>{
        const card = cardTemplate.content.cloneNode(true).children[0]
        const content = card.querySelector("[data-content]")
        content.textContent = user.key
        cardContainer.append(card)
        return { word: user.key, element: card }
    })
})