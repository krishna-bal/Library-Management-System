
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


class LibraryManagement{
    static Library library = new Library();
    public static void main(String[] args) {
        int choice;
        Scanner s= new Scanner(System.in);
        System.out.println("    \n\nLIBRARY MANAGEMENT SYSTEM \n");
        do { 
            System.out.println("-------------------------------------------------------");
            System.out.println("1. Add Book\n2.Add Member\n3.Issue Book\n4.Return Book\n5.Show Books\n6.Show Members\n7.Exit");
            System.out.println("-------------------------------------------------------");
            choice = s.nextInt();
            s.nextLine();
            switch (choice) {
                case 1:
                    library.addBook();
                    break;
                case 2:
                    library.addMember();    
                    break;
                case 3:
                    library.issueBook();
                    break;
                case 4:
                    library.returnBook();
                    break;
                case 5:
                    library.getBooks();
                    break;
                case 6:
                    library.getMembers();
                    break;
                case 7:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        } while (choice!=7);
    }
}

class Book{
    private final String bookId, author, title;
    private int quantity;

    Book(String bookId,String title,String author,int quantity){
        this.bookId=bookId;
        this.title=title;
        this.author=author;
        this.quantity=quantity;
    }
    
    public String getBookId(){
        return bookId;
    }

    public String getTitle(){
        return this.title;
    }

    public String getAuthor() {
        return author;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

abstract class Member{
    private final String memberId, name, type;
    private int booksBorrowed=0;

    Member(String memberId, String name, String type){
        this.memberId=memberId;
        this.name=name;
        this.type=type;
    }

    abstract int getMaxBookLimit();
    abstract float getFinePerDay();

    public int getBooksBorrowed() {
        return booksBorrowed;
    }

    public void setBooksBorrowed(int booksBorrowed) {
        this.booksBorrowed = booksBorrowed;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}

class Student extends Member{
    Student(String memberId, String name, String type){
        super(memberId, name, type);
    }

    private final int limit = 2;
    private final float fine = 10;

    @Override
    public int getMaxBookLimit(){
        return limit;
    }

    @Override
    public float getFinePerDay(){
        return fine;
    }
}

class Faculty extends Member{
    Faculty(String memberId, String name, String type){
        super(memberId, name, type);
    }
    
    private final int limit = 10;
    private final float fine = 3;
    
    public int getMaxBookLimit(){
        return limit;
    }
    
    public float getFinePerDay(){
        return fine;
    }
}

class Library{
    private final HashMap<String,Book> books;
    private final HashMap<String,Member> members;
    private final List<Transaction> issuedBooks;
    private final int maxBorrowLimit=14;

    private Scanner s = new Scanner(System.in);
    public Library() {
        this.books = new HashMap<>();
        this.issuedBooks = new ArrayList<>();
        this.members = new HashMap<>();
    }
    public void addBook(){
        System.out.println("Enter Book ID : ");
        String bookId = s.nextLine();
        System.out.println("Enter Title : ");
        String title = s.nextLine();
        System.out.println("Enter author : ");
        String author = s.nextLine();
        System.out.println("Enter quantity :");
        int quantity = s.nextInt();
        s.nextLine();

        Book book = new Book(bookId, title, author,quantity);
        books.put(bookId, book);
        System.out.println("\nBook Added Successfully.\n");
    }

    public void getBooks() {
        System.out.println("-------------------------------------------------------");
        books.forEach((k, v)-> System.out.println(("\nBook ID : "+v.getBookId()+"\nTitle : "+v.getTitle()+"\nAuthor : "+v.getAuthor()+"\nQuantity : "+v.getQuantity())));
    }

    public void getMembers() {
        System.out.println("-------------------------------------------------------");
        members.forEach((k, v)-> System.out.println(("\nMember ID : "+v.getMemberId()+"\nName : "+v.getName()+"\nBooks Borrowed : "+v.getBooksBorrowed())));
    }

    public void addMember(){
        System.out.println("Enter member ID : ");
        String memberId = s.nextLine();
        System.out.println("Enter name :");
        String name = s.nextLine();
        System.out.println("Enter type (Student / Faculty): ");
        String type = s.nextLine();

        if(type.toLowerCase().equals("student")){
            Member member = new Student(memberId, name, type);
            members.put(memberId, member);
            System.out.println("\nMember "+name+" Added Successfully.\n");
        }
        else if(type.toLowerCase().equals("faculty")){
            Member member = new Faculty(memberId, name, type);
            members.put(memberId, member);
            System.out.println("\nMember "+name+" Added Successfully.\n");
        }
        else{
            System.out.println("Invalid type!");
        }
    }

    void issueBook(){
        System.out.println("Enter Member Id :");
        String memberId = s.nextLine();
        System.out.println("Enter Book Id :");
        String bookId = s.nextLine();
        Book book = books.get(bookId);
        Member member = members.get(memberId);
        if(member == null) {
            System.out.println("Member not found!");
            return;
        }
        if(book!=null){
            if(member.getBooksBorrowed()==member.getMaxBookLimit()){
                System.out.println("Reached max book borrowed limit!");
            }
            else if(book.getQuantity()>0){
                LocalDate issueDate = LocalDate.now();
                LocalDate dueDate = issueDate.plusDays(maxBorrowLimit);
                Transaction t = new Transaction(memberId, bookId, issueDate, dueDate);
                issuedBooks.add(t);
                member.setBooksBorrowed(member.getBooksBorrowed()+1);
                book.setQuantity(book.getQuantity()-1);
                System.out.println("\n"+book.getTitle()+" successfully issued to "+member.getName()+"\n");
            }
            else{
                System.out.println("\nAll books are borrowed!\n");
            }
        }
        else{
            System.out.println("Book not found!");
        }
    }

    void returnBook(){
        System.out.println("Enter Member Id :");
        String memberId = s.nextLine();
        System.out.println("Enter Book Id :");
        String bookId = s.nextLine();
        Book book = books.get(bookId);
        Member member = members.get(memberId);
        Transaction transaction = null;
        for(var t:issuedBooks){
            if(t.getMemberId().equals(member.getMemberId()) && t.getBookId().equals(book.getBookId())){
                transaction=t;
            }
        }
        if(transaction!=null){
            long extraDays = ChronoUnit.DAYS.between(transaction.getDueDate(),LocalDate.now());
            float fine = member.getFinePerDay()*extraDays;
            System.out.println("\n");
            if(fine>0){
                System.out.println("You are "+extraDays+" late!");
                System.out.println("Pay "+fine+" Rupees as fine.");
            }
            book.setQuantity(book.getQuantity()+1);
            issuedBooks.remove(transaction);
            member.setBooksBorrowed(member.getBooksBorrowed()-1);
            System.out.println("Book successfully returned!");
        }
    }

}

class Transaction{
    private final String memberId;
    private final String bookId;
    private final LocalDate issueDate;
    private final LocalDate dueDate;

    public Transaction(String memberId, String bookId, LocalDate issueDate, LocalDate dueDate) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public String getMemberId() {
        return memberId;
    }
    public String getBookId() {
        return bookId;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public LocalDate getIssueDate() {
        return issueDate;
    }
}
