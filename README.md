
# i connect

**i Connect** is a desktop application designed for seamless communication and efficient note management. It enables users to connect with each other through messaging while also allowing them to create, edit, and delete notes.

---

## Features  

### 🔐 User Authentication  
- 🔗 Users must **sign up** before accessing the application.  
- 🔗 After signing up, they must **sign in** to proceed.  

### 📊 Dashboard  
- 🔗 Displays **user information**.  
- 🔗 Shows **famous quotes** retrieved via **API parsing**.  
- 🔗 Provides two main options: **"Keep Notes"** and **"Join Chat"**.  

### 📝 Notes Management  
- 🔗 Users can **create new notes**.  
- 🔗 Edit **previously saved notes**.  
- 🔗 Delete **unwanted notes**.  

### 💬 Chat System  
- 🔗 Users can **send and receive messages**.  
- 🔗 Messages are stored in a **MySQL database**.  
- 🔗 A **polling technique** is used to regularly check for **new messages**.  

---



### ⚒️Technologies Used:  
- 🎨 **JavaFX** (User Interface)  
- 🎭 **CSS** (Styling)  
- 🗄️ **MySQL** (Database)  
- 🌐 **API Parsing** (Fetching Famous Quotes)  
- 🔄 **Polling Technique** (Real-time Message Updates)  
## Run Locally

**Prerequisites**
- Java JDK 20
- JavaFX SDK 20.0.1
- MySQL 8.0.28

1. Clone the project

```bash
  git clone https://github.com/UnpolishedNoob/i-connect.git
```
2. Edit your database info in the project and run.
3. Or you can make a jar file and run using these command

```bash
  mvn clean package
  java -jar target/i_connect.jar

```
## Screenshots  

| Description                                      | Screenshot                                    |
|--------------------------------------------------|-----------------------------------------------|
| **Signin Page**: Signin page to enter into the app. | ![Sign in page](https://github.com/user-attachments/assets/2756f4d8-0aad-47a6-a512-9e660edab724) |
| **Signup Page**: Where users sign up to access the app. | ![Sign up page](https://github.com/user-attachments/assets/cc29eb70-be7d-4f59-b856-e8972c2f5dbf) |
| **Dashboard**: The main dashboard displaying user information and options to keep notes or join chat. | ![Dashboard](https://github.com/user-attachments/assets/f89e9b8b-74d2-49c3-b456-c9d43b0390fe) |
| **Notes Management**: The section where users can create, edit, and delete notes. |![Keep Notes page](https://github.com/user-attachments/assets/14fea991-a63f-4a6f-be6c-4dbc85f5edf4) |
| **Chat System**: The page where users can send and receive messages. | ![Chat page](https://github.com/user-attachments/assets/f98d7ba7-831d-4f2c-99dc-85b8a48662ec) |




## 🚀 About Me
**Pritom Banik**
- GitHub: [UnpolishedNoob](https://github.com/UnpolishedNoob)
- Linkedin: [Pritom Banik](https://www.linkedin.com/in/pritom-banik-272828250/)

 ---
 ## License
[MIT](https://github.com/UnpolishedNoob/i-connect/blob/main/LICENSE)
