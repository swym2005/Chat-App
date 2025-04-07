# Java Swing Chat Application (Client-Server with File Sharing)

This is a simple client-server chat application built using Java and Swing. It supports real-time messaging and document sharing between a client and a server. The application uses TCP sockets for communication and `ObjectInputStream`/`ObjectOutputStream` to support sending and receiving both text and files (of any type, including PDF, DOCX, TXT, XLSX, etc.).

---

## 🚀 Features

- ✅ Real-time text messaging
- 📁 Send and receive files (all types supported)
- 🖼 User interface built with Java Swing
- 📥 Files saved to disk with `received_` prefix
- 🔧 Uses `ObjectOutputStream` and `ObjectInputStream` for full file + text handling
- 🧱 Fully functional on localhost (127.0.0.1) and you can host a server and connect the client in other system bt tweaking the firewall settings in windows.

---

## 🗂️ Project Structure

- The structure is a clone of WhatsApp
- There are static status signs and profile icon labels just to clone the interface.


---

## 💻 How to Run

### 1. Make sure Java is installed:
```bash
java -version
javac -version

### 2. Compile the server and client:
javac Server.java
javac Client.java

### 3. Run the server and Client:
java Server
java Client

###🧪 How It Works
-Messages and files are sent as serialized Java objects.

-The GUI supports:

-Sending text via a text field + send button
-Sending files via a file icon (file chooser opens)
-Files are saved locally with received_ prepended to the filename.

### 📌 Requirements
-Java 8 or later

-The Icons folder must exist in the root directory with the following files:

-back_icon.png
-Profile_icon.png
-file_add.png

###⚠️ Limitations
-No support for multiple clients (1:1 only)
-No authentication/login system
-No message encryption (not secure for production use)

###📚 License
-This project is open-source and available for educational and personal use.
-Feel free to modify, extend, and share!

###🙌 Credits
Created by [Swayam Dalal]
Icons used are assumed to be royalty-free or project-specific.

                        


