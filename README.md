# Java Layered Architecture Application

[![Language](https://img.shields.io/badge/Language-Java_17+-orange.svg)](#)
[![Paradigm](https://img.shields.io/badge/Paradigm-OOP-blue.svg)](#)
[![Architecture](https://img.shields.io/badge/Architecture-3--Tier-brightgreen.svg)](#)

> A robust, desktop-based application built in Java demonstrating advanced Object-Oriented Programming (OOP) principles and structured using a clean, 3-Tier Layered Architecture.

## 🚀 Overview

This project is a [**descriu breument què fa l'app, ex: Store Management System / Multimedia Library**] that allows users to [**quina és l'acció principal, ex: register items, track inventory, and generate reports**]. 

The primary focus of this project is software design and architecture. It heavily utilizes core OOP concepts such as **Encapsulation, Inheritance, Polymorphism, and Abstraction** to ensure the code is modular, scalable, and easy to maintain.

## 🏗️ Architecture & Structure

The application strictly follows a multi-layer architecture to decouple the user interface from the business logic and data access:

* **`Presentation/`**: Handles the User Interface (UI), capturing user inputs and displaying outputs.
* **`Business/`**: Contains the core domain logic, algorithms, and models.
* **`Persistance/`**: Manages data storage, retrieval, and file I/O operations (reading/writing to the `Data/` folder).
* **`Exceptions/`**: Custom error handling ensuring graceful system failures.

## 🛠️ Tech Stack & Tools

* **Language:** Java
* **IDE:** IntelliJ IDEA
* **Design & Modeling:** StarUML (Class Diagrams)
* **Documentation:** JavaDoc

## ⚙️ Quick Start

To run this project locally:

1. Clone the repository:

    git clone https://github.com/yourusername/java-layered-architecture.git

2. Open the project folder in **IntelliJ IDEA**.
3. Ensure the dependencies located in the `Libraries/` folder (e.g., JSON parsers, if any) are added to your project structure (`File > Project Structure > Modules > Dependencies`).
4. Locate `Main.java` inside the `src/` directory and run it.

## 📖 Documentation & UML

Comprehensive system design documentation, including the final project report and StarUML (`.mdj`) Class Diagrams, can be found in the `docs/` directory.