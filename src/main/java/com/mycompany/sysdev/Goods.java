package com.mycompany.sysdev;

import static com.mycompany.sysdev.Common.scanner;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Goods {
    private static List<Product> goodsList = new ArrayList<>();
    private static final String CSV_FILE_PATH = "goods.csv";
    private int maxSerialNumber = 0; // 最大連番
    private String currentDate = ""; // 現在の採番日を保持

    // 商品データをCSVから読み込み
    public void loadGoodsFromCSV() {
        goodsList.clear(); // リストを初期化
        maxSerialNumber = 0; // 最大連番を初期化

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 9) {
                    goodsList.add(new Product(
                        data[0], data[1], data[2], data[3],
                        data[4], data[5], data[6],
                        Integer.parseInt(data[7]), data[8]
                    ));

                    // 最大連番を更新
                    String serialPart = data[0].substring(10); // 商品IDの連番部分を取得
                    int serialNumber = Integer.parseInt(serialPart);
                    if (serialNumber > maxSerialNumber) {
                        maxSerialNumber = serialNumber;
                    }
                } else {
                    System.out.println("不正なフォーマットの行をスキップしました: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("エラー: 商品情報の読み込みに失敗しました。");
        }
    }

    
    // 商品IDを生成
    public String generateProductId() {
        // 今日の日付を取得
        String todayDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        // 日付が変わっていたら連番をリセット
        if (!todayDate.equals(currentDate)) {
            currentDate = todayDate; // 日付を更新
            maxSerialNumber = 0;     // 連番をリセット
        }

        maxSerialNumber++; // 連番をインクリメント
        return "99" + currentDate + String.format("%03d", maxSerialNumber);
    }

    public void saveToCSV(Product product) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, true))) {
            writer.write(product.toCSV());
            writer.newLine(); // 改行を追加
        } catch (IOException e) {
            System.out.println("エラー: 商品情報の保存に失敗しました。");
        }
    }

    public void goodsManageMenu(Scanner scanner) {
        while (true) {
            System.out.println("***** 商品情報管理メニュー *****");
            System.out.println("1: 登録");
            System.out.println("2: 変更/確認/削除");
            System.out.println("3: 一覧表示");
            System.out.println("0: スタッフメインメニューに戻る");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            switch (choice) {
                case 1:
                    registerProduct(scanner);
                    break;
                case 2:
                    modifyOrDeleteProduct();
                    break;
                case 3:
                    displayProductList();
                    break;
                case 0:
                    return; // メインメニューに戻る
                default:
                    System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }

    public void registerProduct(Scanner scanner) {
        while (true) {
            // 新規商品情報入力画面を表示
            System.out.println("***** 新規商品情報入力 *****");
            System.out.println("1 : 情報入力");
            System.out.println("0 : 商品情報管理メニューに戻る");
            System.out.print("入力＞ ");
            int inputChoice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            if (inputChoice == 0) {
                System.out.println("商品情報管理メニューに戻ります。");
                return;
            } else if (inputChoice == 1) {
                // 情報入力画面
                System.out.print("タイトル＞ ");
                String title = scanner.nextLine().trim();
                if (!Common.isValidLength(title, 20)) {
                    System.out.println("タイトルは半角20文字以内で入力してください。");
                    continue;
                }
                System.out.print("ジャンル＞ ");
                String genre = scanner.nextLine().trim();
                if (!Common.isValidLength(genre, 10)) {
                    System.out.println("エラー: ジャンルは半角10文字以内で入力してください。");
                    continue;
                }
                
                String releaseDate;
                while (true) {
                    System.out.println("発売日");
                    System.out.print("年（西暦） ＞ ");
                    String year = scanner.nextLine().trim();
                    System.out.print("月 ＞ ");
                    String month = scanner.nextLine().trim();
                    System.out.print("日 ＞ ");
                    String day = scanner.nextLine().trim();
                    releaseDate = year + String.format("%02d", Integer.parseInt(month)) + String.format("%02d", Integer.parseInt(day));

                    if (Common.isValidDate(year, month, day)) {
                        break;
                    } else {
                        System.out.println("無効な日付です。再入力してください。");
                    }
                }

                String productId = generateProductId();

                // 新規商品情報確認画面を表示
                while (true) {
                    System.out.println("***** 新規商品情報確認 *****");
                    System.out.println("商品ID　　: " + productId);
                    System.out.println("タイトル　: " + title);
                    System.out.println("ジャンル　: " + genre);
                    System.out.println("発売日　　: " + formatDate(releaseDate));
                    System.out.println("1 : 登録");
                    System.out.println("0 : 商品情報管理メニューに戻る");
                    System.out.print("入力＞ ");
                    int confirmChoice = scanner.nextInt();
                    scanner.nextLine(); // 改行を消費

                    if (confirmChoice == 1) {
                        // 商品登録処理
                        Product newProduct = new Product(productId, title, genre, releaseDate, "0", "000000000000", "0", 0, "1");
                        goodsList.add(newProduct);
                        saveToCSV(newProduct);
                        System.out.println("商品が登録されました。");
                        break;
                    } else if (confirmChoice == 0) {
                        System.out.println("商品情報管理メニューに戻ります。");
                        return;
                    } else {
                        System.out.println("無効な入力です。再度入力してください。");
                    }
                }
            } else {
                System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }


    public List<Product> getGoodsList() {
        return goodsList;
    }
    
    public static Product findProductById(String productId) {
        for (Product product : goodsList) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }
    
    // 貸出情報の更新
    public void lendProduct(String productId, String memberId, String dueDate) {
        Product product = findProductById(productId); // リストから商品を検索
        if (product != null) {
            if (!product.getLendStatus().equals("0")) { // 貸出可否をチェック
                System.out.println("この商品は既に貸出中です。");
                return;
            }
            
            String today = getTodayDate();

            // 商品情報を更新
            product.setLendStatus("1"); // 貸出中に設定
            product.setLendMemberId(memberId); // 貸出会員IDを設定
            product.setLendReturnDate(today); // 貸出日を設定
            product.setLendCount(product.getLendCount() + 1); // 貸出回数を増加
            overwriteGoodsToCSV();

            System.out.println("貸出が登録されました。");
        } else {
            System.out.println("商品が見つかりません。");
        }
    }
    private String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }    


    // 返却情報の更新
    public void returnProduct(String productId) {
        Product product = findProductById(productId); // 商品を検索
        if (product != null) {
            if (!product.getLendStatus().equals("1")) { // 貸出中であるかを確認
                System.out.println("この商品は貸出中ではありません。");
                return;
            }

            // 本日日付を取得
            String today = getTodayDate();

            // 商品情報を更新
            product.setLendStatus("0"); // 貸出可に設定
            product.setLendMemberId("000000000000"); // 貸出会員IDをリセット
            product.setLendReturnDate(today); // 返却日付を設定

            // 変更内容をCSVに反映
            overwriteGoodsToCSV();
            System.out.println("返却処理が完了し、CSVファイルに更新されました。");
        } else {
            System.out.println("商品が見つかりません。");
        }
    }    

   private static void modifyOrDeleteProduct() {
        while (true) {
            System.out.print("商品IDを入力してください（戻る: 0）＞ ");
            String productId = scanner.next();

            // 0が入力されたら終了
            if (productId.equals("0")) {
                System.out.println("商品情報管理メニューに戻ります。");
                return;
            }

            Product product = findProductById(productId);
            if (product == null) {
                System.out.println("商品が見つかりません。");
                continue; // 再度入力を求める
            }

            System.out.println("***** 商品情報確認 *****");
            // 商品情報の表示
            displayProductDetails(product);

            // 選択肢の表示
            System.out.println();
            System.out.println("1 : 変更");
            System.out.println("2 : 削除");
            System.out.println("0 : 商品情報管理メニューに戻る");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            switch (choice) {
                case 1:
                    // 商品情報変更入力画面の表示
                    while (true) {
                        System.out.println("***** 商品情報変更入力 *****");
                        System.out.println("1 : 情報変更");
                        System.out.println("0 : 商品情報管理メニューに戻る");
                        System.out.print("入力＞ ");
                        int modifyChoice = scanner.nextInt();
                        scanner.nextLine(); // 改行を消費

                        if (modifyChoice == 1) {
                            modifyProduct(product); // 情報変更
                            break;
                        } else if (modifyChoice == 0) {
                            System.out.println("商品情報管理メニューに戻ります。");
                            return; // メニューに戻る
                        } else {
                            System.out.println("無効な入力です。再度入力してください。");
                        }
                    }
                    break;

                case 2:
                    // 商品削除確認画面の表示
                    if (confirmDeletion(product)) {
                        goodsList.remove(product); // 削除
                        overwriteGoodsToCSV();
                        System.out.println("商品が削除されました。");
                    } else {
                        System.out.println("削除をキャンセルしました。");
                    }
                    break;

                case 0:
                    System.out.println("商品情報管理メニューに戻ります。");
                    return; // メニューに戻る

                default:
                    System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }

    private static void displayProductDetails(Product product) {
        System.out.println("商品ID　　　: " + product.getProductId());
        System.out.println("タイトル　　: " + product.getTitle());
        System.out.println("ジャンル　　: " + product.getGenre());
        System.out.println("発売日　　　: " + formatDate(product.getReleaseDate()));
        System.out.println("貸出・返却日: " + formatDate(product.getLendReturnDate()));

        // 貸出会員IDが "0" の場合は "000000000000" を表示
        System.out.println("貸出会員ID　: " + (product.getLendMemberId().equals("0") ? "000000000000" : product.getLendMemberId()));


        System.out.println("貸出状況　　: " + (product.getLendStatus().equals("0") ? "貸出可" : "貸出中"));
        System.out.println("貸出回数　　: " + product.getLendCount());
        System.out.println("保管場所　　: " + (product.getLocation().equals("1") ? "店頭" : "倉庫"));
    }   
 
        private static void modifyProduct(Product product) {
        System.out.println("変更項目を入力してください。\n未入力の項目は変更されません。");
        
        System.out.println("商品ID　　　: " + product.getProductId());
        System.out.print("タイトル :" + product.getTitle() + " ＞ ");
        //scanner.nextLine(); // 改行文字を消費
        String newTitle = scanner.nextLine().trim();
        if (!newTitle.isEmpty()) product.setTitle(newTitle);

        System.out.print("ジャンル :" + product.getGenre() + " ＞ ");
        String newGenre = scanner.nextLine().trim();
        if (!newGenre.isEmpty()) product.setGenre(newGenre);

        System.out.print("発売日 ('/'を除く8桁):" + product.getReleaseDate() + " ＞ ");
        String newReleaseDate = scanner.nextLine().trim();
        if (!newReleaseDate.isEmpty()) {
            if (Common.isValidDate8(newReleaseDate)) {
                product.setReleaseDate(newReleaseDate);
            } else {
                System.out.println("エラー: 無効な日付です。変更をキャンセルしました。");
            }
        }

        System.out.print("貸出・返却日 ('/'を除く8桁) :" + formatDate(product.getLendReturnDate()) + " ＞ ");
        String newLendReturnDate = scanner.nextLine().trim();
        if (!newLendReturnDate.isEmpty()) {
            if (Common.isValidDate8(newReleaseDate)) {
                product.setLendReturnDate(newLendReturnDate);
            } else {
                System.out.println("エラー: 無効な日付です。変更をキャンセルしました。");
            }
        }        

        System.out.print("貸出会員ID :" + (product.getLendMemberId().equals("0") ? "000000000000" : product.getLendMemberId()) + " ＞ ");
        String newLendMemberId = scanner.nextLine().trim();
        if (!newLendMemberId.isEmpty()) {
            product.setLendMemberId(newLendMemberId);
        } else {
            product.setLendMemberId("000000000000"); // デフォルト値
        }

        System.out.print("貸出状況 (1 : 貸出中、0 : 貸出可): " + (product.getLendStatus().equals("1") ? "貸出中" : "貸出可") + " ＞ ");
        String newLendStatus = scanner.nextLine().trim();
        if (!newLendStatus.isEmpty()) product.setLendStatus(newLendStatus);

        System.out.print("貸出回数 :" + product.getLendCount() + " ＞ ");
        String newLendCount = scanner.nextLine().trim();
        if (!newLendCount.isEmpty()) product.setLendCount(Integer.parseInt(newLendCount));

        System.out.print("保管場所 (1 : 店頭、0 : 倉庫) :" + (product.getLocation().equals("1") ? "店頭" : "倉庫") + " ＞ ");
        String newLocation = scanner.nextLine().trim();
        if (!newLocation.isEmpty()) product.setLocation(newLocation);

        // 商品情報確認画面の表示
        displayProductDetails(product);

        // 確認画面
        while (true) {
            System.out.println("1 : 変更");
            System.out.println("0 : 商品情報入力画面に戻る");
            System.out.print("入力＞ ");
            int confirmChoice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            if (confirmChoice == 1) {
                overwriteGoodsToCSV();
                System.out.println("商品情報が変更されました。\n\n");
                break;
            } else if (confirmChoice == 0) {
                System.out.println("変更を取り消しました。商品情報入力画面に戻ります。");
                return;
            } else {
                System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }

   private static void displayProductList() {
        while (true) {
            System.out.println("***** 商品一覧表示項目選択 *****");
            System.out.println("どの項目で並び替えて表示しますか？");
            System.out.println("1 : 商品ID");
            System.out.println("2 : 保管場所");
            System.out.println("3 : 発売日");
            System.out.println("4 : 貸出・返却日");
            System.out.println("0 : 商品情報管理メニューに戻る");
            System.out.print("入力＞ ");
            int sortField = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            if (sortField == 0) {
                // 商品情報管理メニューに戻る
                System.out.println("商品情報管理メニューに戻ります。");
                return;
            }

            System.out.println("***** 商品一覧表示順選択 *****");
            System.out.println("1 : 昇順");
            System.out.println("2 : 降順");
            System.out.println("0 : 商品情報管理メニューに戻る");
            System.out.print("入力＞ ");
            int sortOrder = scanner.nextInt();
            scanner.nextLine(); 

            if (sortOrder == 0) {
                // 商品情報管理メニューに戻る
                System.out.println("商品情報管理メニューに戻ります。");
                return;
            }

            // 商品リストのソート
            List<Product> sortedProducts = new ArrayList<>(goodsList);
            sortedProducts.sort((p1, p2) -> compareProducts(p1, p2, sortField, sortOrder));

            // ページネーションの処理
            displayProductPages(sortedProducts);

            // ページネーション後の戻り処理
            System.out.println("商品情報管理メニューに戻ります。");
            return;
        }
    }
    private static void displayProductPages(List<Product> products) {
        int totalProducts = products.size();
        int pageSize = 20;
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        int currentPage = 1;

        while (true) {
            System.out.println("***** 商品情報一覧 *****");
            System.out.printf("%-4s %-13s %-10s %-10s %-10s %-15s %-8s %-6s%n",
                    "No.", "商品ID", "タイトル", "ジャンル", "発売日", "貸出・返却日", "貸出状況", "保管場所");

            int start = (currentPage - 1) * pageSize;
            int end = Math.min(start + pageSize, totalProducts);

            for (int i = start; i < end; i++) {
                Product product = products.get(i);
                System.out.printf("%-4d %-13s %-10s %-10s %-10s %-15s %-8s %-6s%n",
                        i + 1,
                        product.getProductId(),
                        product.getTitle(),
                        product.getGenre(),
                        formatDate(product.getReleaseDate()),
                        formatDate(product.getLendReturnDate()),
                        product.getLendStatus().equals("1") ? "貸出中" : "貸出可",
                        product.getLocation().equals("1") ? "店頭" : "倉庫");
            }

            // ページ選択メニュー
            System.out.println();
            System.out.println("1:次ページを表示");
            System.out.println("0:商品情報管理メニューに戻る");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            if (choice == 0) {
                // 商品情報管理メニューに戻る
                System.out.println("商品情報管理メニューに戻ります。");
                return;
            } else if (choice == 1) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("これ以上次のページはありません。");
                }
            } else {
                System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }   
    
    private static boolean confirmDeletion(Product product) {
        System.out.println("***** 商品情報削除確認 *****");
        System.out.println("削除してもよろしいですか？(Y/N)");
        System.out.print("入力＞ ");

        while (true) {
            String input = scanner.nextLine().trim().toUpperCase(); // 入力を大文字に変換
            if (input.equals("Y")) {
                return true; // 削除を実行
            } else if (input.equals("N")) {
                return false; // 削除をキャンセル
            } else {
                System.out.println("無効な入力です。'Y' または 'N' を入力してください。");
            }
        }
    }    
    private static String formatDate(String date) {
        if (date == null || date.length() != 8 || date.equals("0")) {
            return "0000/00/00"; // 不正な日付の場合のデフォルト値
        }
        return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
    }

    private static int compareProducts(Product p1, Product p2, int sortField, int sortOrder) {
        int result = 0;

        switch (sortField) {
            case 1: // 商品ID
                result = p1.getProductId().compareTo(p2.getProductId());
                break;
            case 2: // 保管場所
                result = p1.getLocation().compareTo(p2.getLocation());
                break;
            case 3: // 発売日
                result = p1.getReleaseDate().compareTo(p2.getReleaseDate());
                break;
            case 4: // 貸出・返却日
                result = p1.getLendReturnDate().compareTo(p2.getLendReturnDate());
                break;
            default:
                throw new IllegalArgumentException("無効な並び替え項目です。");
        }

        // 昇順または降順の適用
        return sortOrder == 1 ? result : -result;
    }   
    public static void overwriteGoodsToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH))) {
            for (Product product : goodsList) {
                writer.write(product.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("エラー: CSVファイルの上書きに失敗しました。");
        }
    }    
    
    
    public static class Product {
        private String productId;
        private String title = "0";
        private String genre = "0";
        private String releaseDate = "0";
        private String lendReturnDate = "0";
        private String lendMemberId = "0";
        private String lendStatus = "0";
        private int lendCount = 0;
        private String location = "1"; // 店頭 : 1

        public Product(String productId, String title, String genre, String releaseDate, String lendReturnDate,
                       String lendMemberId, String lendStatus, int lendCount, String location) {
            this.productId = productId != null ? productId : "0";
            this.title = title != null ? title : "0";
            this.genre = genre != null ? genre : "0";
            this.releaseDate = releaseDate != null ? releaseDate : "0";
            this.lendReturnDate = lendReturnDate != null ? lendReturnDate : "0";
            this.lendMemberId = lendMemberId != null ? lendMemberId : "0";
            this.lendStatus = lendStatus != null ? lendStatus : "0";
            this.lendCount = lendCount;
            this.location = location != null ? location : "1";
        }
        
        public String toCSV() {
            return String.join(",", productId, title, genre, releaseDate, lendReturnDate, lendMemberId, lendStatus,
                    String.valueOf(lendCount), location);
        }
        
        public boolean isNew() {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate releaseDate = LocalDate.parse(this.getReleaseDate(), formatter);
                LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

                // 発売日が6か月以内なら新作、それ以外は旧作
                return releaseDate.isAfter(sixMonthsAgo) || releaseDate.isEqual(sixMonthsAgo);
            } catch (Exception e) {
                System.out.println("日付の判定中にエラーが発生しました。");
                return false; // デフォルトで旧作扱い
            }
        }

        public String getProductId() { return productId; }
        public String getTitle() { return title; }
        public String getGenre() { return genre; }
        public String getReleaseDate() { return releaseDate; }
        public String getLendReturnDate() { return lendReturnDate; }
        public String getLendMemberId() {
            return lendMemberId != null ? lendMemberId : "000000000000"; // nullチェックとデフォルト値設定
        }
        public String getLendStatus() { return lendStatus; }
        public int getLendCount() { return lendCount; }
        public String getLocation() { return location; }

        public void setTitle(String title) { this.title = title; }
        public void setGenre(String genre) { this.genre = genre; }
        public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
        public void setLendMemberId(String lendMemberId) {
            this.lendMemberId = lendMemberId;
        }
        public void setLendReturnDate(String lendReturnDate) { this.lendReturnDate = lendReturnDate; }
        public void setLendStatus(String lendStatus) { this.lendStatus = lendStatus; }
        public void setLendCount(int lendCount) { this.lendCount = lendCount; }
        public void setLocation(String location) { this.location = location; }
        
        @Override
        public String toString() {
            return "商品ID: " + productId + ", タイトル: " + title + ", ジャンル: " + genre + ", 発売日: " + releaseDate;
        }         
    }      
}
