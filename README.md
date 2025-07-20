# Visitor Management System

This is a full-stack visitor tracking system I built using Java for the backend and HTML/CSS/JavaScript for the frontend. The goal was to create a clean, easy-to-understand workflow where hosts can approve visitors, QR codes are generated, and everything runs offline using flat JSON files. 
I avoided heavy libraries and focused on writing my own logic so I could explain every part of it.
I uses concept of OOPS .

---

## Features

- **Host Login/Register**
  - Hosts can create accounts and log in securely.
  - Only logged-in hosts can manage requests.

- **Visitor Registration**
  - Visitors submit their name, email, photo, and purpose.
  - Photo is captured using webcam (vanilla JS + canvas).
  - Requests go to a pending list visible to the host.

- **Approval & QR Code**
  - Hosts can approve or reject requests.
  - Approved visitors get a unique QR code.
  -After getting approval from host request from pendingrequests.json goes in visitors.json and from pendingvisitors.json this entry gets deleted


Once a visitor is approved by the host:

- They appear in the dashboard inside checked in visitor list that guard can check at entry time
- Each badge shows:
  - Visitor **name, email, organization, host, purpose**
  - Captured **photo** (via webcam)
  - **QR code** unique to that visitor
  
  - Logout button to mark them as checked-out
  - Logout timestamp will be added in backend(visitors.json)

The badges are styled to be clear and minimal, making it easy for guard to manage traffic and access control.

---

 Pre-Approval with QR 

Hosts can also **pre-approve** visitors before the visit:

- They enter visitor details manually
- The system generates a QR code immediately




- **Logout Functionality**
  - Hosts can manually log out visitors using mobile numbers.
  - This updates the JSON and hides them from the active list.

---

##  Tech Stack

| Layer     | Tool/Language                    

| Backend   | Java (Spark), Gson (JSON parsing)
| Frontend  | HTML, CSS, JS            
| Storage   | Flat `.json` files               
| QR Logic  | Custom Java-based image generator
| UI Style  | Minimal hand-built CSS           

---

## 📁 Project Structure
frontend/ ├─ index.html ├─ hostLogin.html ├─ hostRegister.html ├─ host.html ├─ style.css |-script.js

backend/ ├─ App.java ├─ Visitor.java ├─ VisitorService.java ├─ Host.java ├─ QRGenerator.java ├─ visitors.json ├─ pendingvisitors.json |-hosts.json

---



##  Why I Built It

I wanted to create a visitor tracking system that I could fully understand and explain from end to end. Instead of using heavy frameworks or shortcuts, I wrote everything myself—from the backend routes and QR logic to the frontend layout and photo capture flow. This project reflects my ability to build under pressure, debug without panic, and design features that feel intentional.

I avoided tools I couldn’t confidently defend, and made sure every line of code was something I actually understood. That way, if someone reviews my work or asks about any feature, I can walk them through it with clarity.

This system shows my approach to building real solutions: practical, reliable, and explainable.


## How to Run

1. Open `/backend` in IntelliJ or any Java IDE  
   Run `App.java` (Spark handles the routes)

2. Open any of the HTML files in your browser (preferably Chrome)

3. All data gets saved to `.json` files locally



---

##  Future Improvements

- QR scanning via webcam  
- More refined access control  
- Admin dashboard with analytics  
- Better mobile styling

---


