const products = [
    {
        id: 1,
        name: "Wireless Headphones",
        price: 1499,
        description: "Enjoy your favorite music.",
        image: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop"
    },
    {
        id: 2,
        name: "Smart Watch",
        price: 2499,
        description: "Stay connected every day.",
        image: "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop"
    },
    {
        id: 3,
        name: "Camera",
        price: 3999,
        description: "Capture your special moments.",
        image: "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&auto=format&fit=crop"
    },
    {
        id: 4,
        name: "Running Shoes",
        price: 1999,
        description: "Comfort for everyday walks.",
        image: "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop"
    },
    {
        id: 5,
        name: "Backpack",
        price: 999,
        description: "Carry your essentials with ease.",
        image: "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&auto=format&fit=crop"
    },
    {
        id: 6,
        name: "Sunglasses",
        price: 799,
        description: "A classic everyday accessory.",
        image: "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600&auto=format&fit=crop"
    }
];

let cart = [];

function displayProducts(items) {
    const list = document.getElementById("product-list");
    const emptyMessage = document.getElementById("empty-message");

    list.innerHTML = "";

    emptyMessage.hidden = items.length !== 0;

    items.forEach(product => {
        const card = document.createElement("article");
        card.className = "product";

        const img = document.createElement("img");
        img.className = "product-image";
        img.src = product.image;
        img.alt = product.name;
        img.loading = "lazy";

        // Show a placeholder if an online image fails.
        img.onerror = function () {
            this.onerror = null;
            this.src = "https://placehold.co/600x400/f1f5f9/172554?text=ShopSphere+Product";
        };

        const name = document.createElement("h3");
        name.textContent = product.name;

        const description = document.createElement("p");
        description.className = "description";
        description.textContent = product.description;

        const price = document.createElement("p");
        price.className = "price";
        price.textContent = "₹" + product.price.toLocaleString("en-IN");

        const button = document.createElement("button");
        button.textContent = "Add to Cart";
        button.addEventListener("click", () => addToCart(product.id));

        card.append(img, name, description, price, button);
        list.appendChild(card);
    });
}

function addToCart(id) {
    const product = products.find(item => item.id === id);
    const existingItem = cart.find(item => item.id === id);

    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({ ...product, quantity: 1 });
    }

    updateCartCount();
    alert(product.name + " added to your cart!");
}

function updateCartCount() {
    const count = cart.reduce((total, item) => total + item.quantity, 0);
    document.getElementById("cart-count").textContent = count;
}

document.getElementById("search").addEventListener("input", function () {
    const query = this.value.trim().toLowerCase();

    const filteredProducts = products.filter(product =>
        product.name.toLowerCase().includes(query)
    );

    displayProducts(filteredProducts);
});

document.getElementById("cart-button").addEventListener("click", function () {
    if (cart.length === 0) {
        alert("Your cart is empty.");
        return;
    }

    const details = cart.map(item =>
        `${item.name} x ${item.quantity} = ₹${item.price * item.quantity}`
    );

    const total = cart.reduce(
        (sum, item) => sum + item.price * item.quantity, 0
    );

    alert(
        "YOUR SHOPSPHERE CART\n\n" +
        details.join("\n") +
        "\n\nTotal: ₹" + total.toLocaleString("en-IN")
    );
});

displayProducts(products);