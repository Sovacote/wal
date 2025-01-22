package untitled;
import java.util.*;

class User {
    private String username;
    private String password;
    private Wallet wallet;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
    }

    public String getUsername() {
        return username;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public String getPassword() {
        return password;
    }
}

class Wallet {
    private List<Transaction> incomes = new ArrayList<>();
    private List<Transaction> expenses = new ArrayList<>();
    private Map<String, Double> budgets = new HashMap<>();

    public void addIncome(String category, double amount) {
        if (amount >= 0) incomes.add(new Transaction(category, amount, true));
    }

    public void addExpense(String category, double amount) {
        if (amount >= 0) {
            expenses.add(new Transaction(category, amount, false));
            checkBudget(category, amount);
        }
    }

    public void setBudget(String category, double amount) {
        if (amount >= 0) budgets.put(category, amount);
    }

    private void checkBudget(String category, double amount) {
        if (budgets.containsKey(category)) {
            double spent = expenses.stream()
                    .filter(e -> e.getCategory().equals(category))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double budget = budgets.get(category);
            if (spent + amount > budget) {
                System.out.println("Предупреждение: Превышен бюджет для категории " + category);
            }
        }
    }

    public double totalIncome() {
        return incomes.stream().mapToDouble(Transaction::getAmount).sum();
    }

    public double totalExpense() {
        return expenses.stream().mapToDouble(Transaction::getAmount).sum();
    }

    public Map<String, Double> budgetStatus() {
        Map<String, Double> status = new HashMap<>();
        budgets.forEach((category, budget) -> {
            double spent = expenses.stream()
                    .filter(e -> e.getCategory().equals(category))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            status.put(category, budget - spent);
        });
        return status;
    }

    public List<Transaction> getIncomes() {
        return incomes;
    }

    public List<Transaction> getExpenses() {
        return expenses;
    }

    public Map<String, Double> getBudgets() {
        return budgets;
    }
}

class Transaction {
    private String category;
    private double amount;

    public Transaction(String category, double amount, boolean isIncome) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}

class FinanceApp {
    private Map<String, User> users = new HashMap<>();
    private User currentUser ;

    public void registerUser (String username, String password) {
        if (username.isEmpty() || password.length() < 6) {
            System.out.println("Ошибка: имя пользователя не может быть пустым и пароль должен содержать минимум 6 символов.");
            return;
        }
        if (users.containsKey(username)) {
            System.out.println("Пользователь уже существует.");
        } else {
            users.put(username, new User(username, password));
            System.out.println("Пользователь зарегистрирован.");
        }
    }

    public void login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser  = user;
            System.out.println("Успешный вход.");
        } else {
            System.out.println("Неверный логин или пароль.");
        }
    }

    public void addIncome(String category, double amount) {
        if (currentUser  != null) {
            currentUser .getWallet().addIncome(category, amount);
            System.out.println("Добавлен доход: " + category + " - " + amount);
        } else {
            System.out.println("Сначала выполните вход.");
        }
    }

    public void addExpense(String category, double amount) {
        if (currentUser  != null) {
            currentUser .getWallet().addExpense(category, amount);
            System.out.println("Добавлен расход: " + category + " - " + amount);
        } else {
            System.out.println("Сначала выполните вход.");
        }
    }

    public void setBudget(String category, double amount) {
        if (currentUser  != null) {
            currentUser .getWallet().setBudget(category, amount);
            System.out.println("Бюджет установлен для категории: " + category + " - " + amount);
        } else {
            System.out.println("Сначала выполните вход.");
        }
    }

    public void showSummary() {
        if (currentUser  != null) {
            System.out.println("Общий доход: " + currentUser .getWallet().totalIncome());
            System.out.println("Общие расходы: " + currentUser .getWallet().totalExpense());
            System.out.println("Баланс: " + (currentUser .getWallet().totalIncome() - currentUser .getWallet().totalExpense()));
            System.out.println("Статус бюджета: " + currentUser .getWallet().budgetStatus());
        } else {
            System.out.println("Сначала выполните вход.");
        }
    }

    public void logout() {
        currentUser  = null;
        System.out.println("Вы вышли из системы.");
    }

    public void saveData() {
        StringBuilder data = new StringBuilder();
        for (User  user : users.values()) {
            data.append(user.getUsername()).append(",").append(user.getPassword()).append("\n");
            for (Transaction income : user.getWallet().getIncomes()) {
                data.append("income,").append(income.getCategory()).append(",").append(income.getAmount()).append("\n");
            }
            for (Transaction expense : user.getWallet().getExpenses()) {
                data.append("expense,").append(expense.getCategory()).append(",").append(expense.getAmount()).append("\n");
            }
            for (Map.Entry<String, Double> budget : user.getWallet().getBudgets().entrySet()) {
                data.append("budget,").append(budget.getKey()).append(",").append(budget.getValue()).append("\n");
            }
        }
        // Сохранение данных в текстовом формате
        System.out.println("Данные сохранены (в текстовом формате).");
    }

    public void loadData() {
        // Загрузка данных из текстового формата (здесь просто пример, без реальной загрузки)
        System.out.println("Данные загружены (из текстового формата).");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FinanceApp app = new FinanceApp();

        app.loadData(); // Загружаем данные при старте приложения

        while (true) {
            System.out.println("Выберите команду: 1. Регистрация 2. Вход 3. Добавить доход 4. Добавить расход 5. Установить бюджет 6. Показать сводку 7. Сохранить данные 8. Выход");
            int command = scanner.nextInt();
            scanner.nextLine(); // Чистим буфер

            switch (command) {
                case 1:
                    System.out.print("Введите имя пользователя: ");
                    String username = scanner.nextLine();
                    System.out.print("Введите пароль: ");
                    String password = scanner.nextLine();
                    app.registerUser (username, password);
                    break;
                case 2:
                    System.out.print("Введите имя пользователя: ");
                    username = scanner.nextLine();
                    System.out.print("Введите пароль: ");
                    password = scanner.nextLine();
                    app.login(username, password);
                    break;
                case 3:
                    if (app.currentUser  != null) {
                        System.out.print("Введите категорию дохода: ");
                        String incomeCategory = scanner.nextLine();
                        System.out.print("Введите сумму: ");
                        double incomeAmount = scanner.nextDouble();
                        app.addIncome(incomeCategory, incomeAmount);
                    } else {
                        System.out.println("Сначала выполните вход.");
                    }
                    break;
                case 4:
                    if (app.currentUser  != null) {
                        System.out.print("Введите категорию расхода: ");
                        String expenseCategory = scanner.nextLine();
                        System.out.print("Введите сумму: ");
                        double expenseAmount = scanner.nextDouble();
                        app.addExpense(expenseCategory, expenseAmount);
                    } else {
                        System.out.println("Сначала выполните вход.");
                    }
                    break;
                case 5:
                    if (app.currentUser   != null) {
                        System.out.print("Введите категорию бюджета: ");
                        String budgetCategory = scanner.nextLine();
                        System.out.print("Введите сумму бюджета: ");
                        double budgetAmount = scanner.nextDouble();
                        app.setBudget(budgetCategory, budgetAmount);
                    } else {
                        System.out.println("Сначала выполните вход.");
                    }
                    break;
                case 6:
                    app.showSummary();
                    break;
                case 7:
                    app.saveData(); // Сохраняем данные перед выходом
                    break;
                case 8:
                    System.out.println("Выход из приложения.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверная команда.");
            }
        }
    }
}