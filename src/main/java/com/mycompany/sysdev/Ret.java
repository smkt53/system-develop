/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sysdev;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Ret {
    private static Scanner scanner = new Scanner(System.in);
    private static Goods goodsManager = new Goods();
    private static List<Goods.Product> returnList = new ArrayList<>(); // 返却商品リスト

public static void retMenu() {
    returnList.clear(); // 返却リストを初期化

    while (true) {
        // 商品ID入力画面 (D-1111)
        System.out.println("商品ID(13桁)を入力してください。(終了:1/戻る:0)＞ ");
        String productId = scanner.nextLine().trim();

        if (productId.equals("1")) {
            break; // 返却リスト確認へ進む
        } else if (productId.equals("0")) {
            System.out.println("返却処理をキャンセルしました。");
            return; // レンタルメニューに戻る
        }

        Goods.Product product = Goods.findProductById(productId);
        if (product == null) {
            System.out.println("商品が見つかりません。再度入力してください。");
        } else if (product.getLendStatus().equals("0")) {
            System.out.println("この商品はすでに返却されています。");
        } else {
            // 返却商品情報確認画面 (D-1112)
            while (true) {
                String today = getTodayDate();
                int lateFee = calculateLateFee(product.getLendReturnDate(), today);

                // 延滞状態の判定
                String overdueStatus = (lateFee > 0) ? "延滞あり" : "延滞なし";

                // 返却可否の判定
                String returnStatus = product.getLendStatus().equals("1") ? "返却可" : "返却不可";

                System.out.println("***** 返却商品情報確認 *****");
                System.out.println(product.getProductId() + ":" + product.getTitle() + ":" + overdueStatus + ":" + returnStatus);
                System.out.println("1:返却リストに登録");
                System.out.println("2:再入力");
                System.out.println("0:返却処理を中断");
                System.out.print("入力＞ ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // 改行を消費

                if (choice == 1) {
                    returnList.add(product);
                    System.out.println("商品が返却リストに登録されました。");
                    break; // 次の商品入力に進む
                } else if (choice == 2) {
                    System.out.println("再入力画面に戻ります。");
                    break; // 商品ID入力画面に戻る
                } else if (choice == 0) {
                    System.out.println("返却処理を中断しました。");
                    return; // レンタルメニューに戻る
                } else {
                    System.out.println("無効な入力です。再度入力してください。");
                }
            }
        }
    }

    // 返却商品リスト確認画面 (D-1114)
    if (displayReturnList()) {
        completeReturnProcess();
        System.out.println("返却処理が完了しました。");
    } else {
        System.out.println("返却をキャンセルしました。");
    }
}

    private static boolean displayReturnList() {
    System.out.println("***** 返却商品リスト確認 *****");

    // 商品IDで昇順ソート
    returnList.sort((p1, p2) -> p1.getProductId().compareTo(p2.getProductId()));

    int index = 1; // リスト番号
    int totalLateFee = 0; // 延滞料金の合計

    for (Goods.Product product : returnList) {
        String today = getTodayDate();
        int lateFee = calculateLateFee(product.getLendReturnDate(), today);
        totalLateFee += lateFee;

        // 延滞状態の判定
        String overdueStatus = (lateFee > 0) ? "延滞あり" : "延滞なし";

        // 商品情報を表示
        System.out.println(index + ". " + product.getProductId() + ":" + product.getTitle() + ":" + overdueStatus);
        index++;
    }

    System.out.println("延滞料金合計: " + totalLateFee + "円");

    // 返却確認プロンプト
    System.out.print("返却しますか?(Y/N)> ");
    String input = scanner.nextLine().trim().toUpperCase();
    return input.equals("Y");
}


    private static void completeReturnProcess() {
        String today = getTodayDate(); // 本日日付を取得
        for (Goods.Product product : returnList) {
            product.setLendStatus("0"); // 貸出可に設定
            product.setLendMemberId("000000000000"); // 貸出会員IDをリセット
            product.setLendReturnDate(today); // 返却日を設定
        }
        Goods.overwriteGoodsToCSV(); // 変更をCSVに反映
    }
    
    private static int calculateLateFee(String lendDate, String returnDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate lendDay = LocalDate.parse(lendDate, formatter);
            LocalDate dueDate = lendDay.plusDays(7); // 貸出日 + 7日
            LocalDate returnDay = LocalDate.parse(returnDate, formatter);

            if (returnDay.isAfter(dueDate)) {
                long daysLate = ChronoUnit.DAYS.between(dueDate, returnDay);
                return (int) daysLate * 100; // 1日あたり100円の延滞料金
            }
        } catch (Exception e) {
            System.out.println("日付の計算に失敗しました。");
        }
        return 0; // 延滞がない場合は0円
    } 

    private static String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}