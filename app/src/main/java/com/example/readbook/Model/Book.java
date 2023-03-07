package com.example.readbook.Model;

public class Book {
  private String nameBook;
  private String urlImageBook;
  private String categoryBook;
  private String updateBook;

    public Book(String nameBook, String urlImageBook, String categoryBook, String updateBook) {
        this.nameBook = nameBook;
        this.urlImageBook = urlImageBook;
        this.categoryBook = categoryBook;
        this.updateBook = updateBook;
    }

    public String getNameBook() {
        return nameBook;
    }

    public void setNameBook(String nameBook) {
        this.nameBook = nameBook;
    }

    public String getUrlImageBook() {
        return urlImageBook;
    }

    public void setUrlImageBook(String urlImageBook) {
        this.urlImageBook = urlImageBook;
    }

    public String getCategoryBook() {
        return categoryBook;
    }

    public void setCategoryBook(String categoryBook) {
        this.categoryBook = categoryBook;
    }

    public String getUpdateBook() {
        return updateBook;
    }

    public void setUpdateBook(String updateBook) {
        this.updateBook = updateBook;
    }
}
