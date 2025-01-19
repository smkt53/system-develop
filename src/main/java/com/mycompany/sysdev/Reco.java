package com.mycompany.sysdev;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Reco {
    private static Scanner scanner = new Scanner(System.in);

    private static String goodId;
    private static String line;
    private static String delimiter = ",";

    public static void Recommend() {
        try (BufferedReader br = new BufferedReader(new FileReader("goods.csv"))) {
            while ((line = br.readLine()) != null) {
                // CSV行を分割
                String[] columns = line.split(delimiter);

                // 商品名（2列目）を取得して表示
                if (columns.length > 1) { // 2列目が存在するか確認
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            System.out.println("***** おすすめ検索 *****");
            System.out.println("1: おすすめジャンル");
            System.out.println("2: おすすめシリーズ");
            System.out.println("0: スタッフメインメニューに戻る");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            switch (choice) {
                case 1:
                    goodId = genre();
                    if (goodId != null) {
                        displayRecommendedProduct(goodId);
                        returnToRecommend();
                    }
                    break;
                case 2:
                    goodId = series();
                    if (goodId != null) {
                        displayRecommendedProduct(goodId);
                        returnToRecommend();
                    }
                    break;
                case 0:
                    return; // メインメニューに戻る
                default:
                    System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }

    public static String genre() {
        while (true) {
            System.out.println("***** おすすめジャンル検索 *****");
            System.out.println("1: アニメ");
            System.out.println("2: ドキュメント");
            System.out.println("0: 戻る");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            switch (choice) {
                case 1:
                    return "9920241120001"; // アニメのおすすめ商品ID
                case 2:
                    return "9920241120008"; // ドキュメントのおすすめ商品ID
                case 0:
                    return null; // メインメニューに戻る
                default:
                    System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }

    public static String series() {
        while (true) {
            System.out.println("***** おすすめシリーズ検索 *****");
            System.out.println("1: くれよんしんちゃんシリーズ");
            System.out.println("2: さざえさんシリーズ");
            System.out.println("0: 戻る");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            switch (choice) {
                case 1:
                    return "9920241120001";
                case 2:
                    return "9920241120003";
                case 0:
                    return null; // メインメニューに戻る
                default:
                    System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }

    public static void displayRecommendedProduct(String productId) {
        try (BufferedReader br = new BufferedReader(new FileReader("goods.csv"))) {
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(delimiter);
                if (columns.length > 0 && columns[0].equals(productId)) {
                    displayProductInfo(columns);
                    return;
                }
            }
            System.out.println("商品が見つかりませんでした。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void displayProductInfo(String[] columns) {
        // 商品情報を出力
        String productId = columns[0];       // 商品ID
        String productName = columns[1];     // 商品名
        String releaseDate = columns[3];     // 発売日
        String borrowReturnDate = columns[4];// 貸出・返却日
        String borrowStatus = columns[5];    // 貸出状況
        String storageLocation = columns[6]; // 保管場所

        System.out.println("商品ID: " + productId);
        System.out.println("商品名: " + productName);
        System.out.println("発売日: " + releaseDate);
        System.out.println("貸出・返却日: " + borrowReturnDate);
        System.out.println("貸出状況: " + borrowStatus);
        System.out.println("保管場所: " + storageLocation);
        System.out.println("---------------------------");
    }

    public static void returnToRecommend(){
        System.out.println("0: おすすめ検索メニューに戻る");
        System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            //無意味だけど一応実装
            //絶対にいらない
            //絶対にいらない
            //絶対にいらない
            //表示して戻ればいいものをなぜ入力させる
            switch (choice) {
                case 0:
                    return; // メインメニューに戻る
                default:
                    return; // メインメニューに戻る
            }
    }
}
