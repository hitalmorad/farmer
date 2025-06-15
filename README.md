
# 🌾 FramDirect – Direct Farm-to-Consumer Marketplace

**FramDirect** is a mobile application designed to empower **farmers** by connecting them directly with **consumers** and **retailers**, eliminating the need for intermediaries. This platform enables farmers to **list their fresh produce**, **negotiate prices**, and **manage transactions** — ensuring fair pricing and better income.

---

## 📱 Features

### 👨‍🌾 For Farmers
- 📝 **List Produce Easily**: Add products with images, quantity, and price per unit.
- 📦 **Update Availability**: Manage inventory and mark items as sold or out of stock.

### 🛒 For Consumers & Retailers
- 🛍️ **Browse Fresh Produce**: Explore available farm products by category or location.
- 🔗 **Directly Connect with Farmers**: Chat and negotiate prices in real-time.
- 📋 **Place Orders & Track Status**: View order status, delivery dates, and previous purchases.
- 💳 **Secure Payments**: Pay using multiple payment options directly within the app.

---

## 🚀 How It Works

1. Farmers register and create a profile with basic farm details.
2. Farmers list their products (e.g., vegetables, grains, fruits) with images, prices, and quantity.
3. Buyers browse the marketplace, filter by category/location, and initiate chat.
4. Negotiation and transaction occur through in-app chat or order requests.
5. Orders are placed, and payment is made through the app or upon delivery.
6. Both parties can rate each other to build trust in the community.

---

## 🛠️ Tech Stack

- **Frontend**: Kotlin, Jetpack Compose
- **Backend**: Firebase (Authentication, Firestore, Firebase Storage)
- **Image Uploads**: Cloudinary API
- **Architecture**: MVVM (Model-View-ViewModel)

---

## 📦 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/agrolink.git
cd agrolink
```

### 2. Open in Android Studio

- Launch Android Studio
- Open the project from the cloned directory

### 3. Setup Firebase

- Add `google-services.json` file in the `/app` directory
- Enable:
  - Firestore Database
  - Firebase Authentication (Email/Password)
  - Firebase Storage

### 4. Configure Cloudinary

- Add your Cloudinary API key and secret in the environment variables or constants file

### 5. Run the App

```bash
./gradlew build
```
or click on **Run ▶️** in Android Studio.

---

## 🔐 Permissions Required

- 📷 Camera and Storage: For uploading product images
- 🌐 Internet Access: For data sync and communication
- 📍 Location (Optional): To filter nearby farmers

---

## 🧑‍💻 Contributors

- [Hital Morad](https://github.com/hitalmorad) – Android Development, Firebase Integration, UI/UX Design

---

## 🤝 Acknowledgements

- [Firebase](https://firebase.google.com/)
- [Cloudinary](https://cloudinary.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)


---

## 📈 Future Enhancements

- 🎤 Voice listing for farmers with limited literacy
- 📉 Real-time market price comparison
- 🌐 Multilingual support (Hindi, Gujarati, etc.)
- 📍 Delivery tracking with GPS
- 🤖 AI-based price suggestion

---


