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

public class Lend {
    private static Scanner scanner = new Scanner(System.in);
    private static Goods goodsManager = new Goods();
    private static MemberManager memberManager = new MemberManager();
    private static List<Goods.Product> cart = new ArrayList<>(); // 貸出商品リスト
    private static int totalAmount = 0; // 合計金額

    public static void lendMenu() {
        List<Goods.Product> lendList = new ArrayList<>();
        int totalFee = 0;

        while (true) {
            // 商品ID入力画面
            System.out.println("商品ID(13桁)を入力してください。(終了:1/戻る:0)＞ ");
            String productId = scanner.nextLine().trim();

            if (productId.equals("1")) {
                break; // 貸出商品リスト確認へ進む
            } else if (productId.equals("0")) {
                System.out.println("貸出処理をキャンセルしました。");
                return; // メニューに戻る
            }

            Goods.Product product = Goods.findProductById(productId);
            if (product == null) {
                System.out.println("商品が見つかりません。再度入力してください。");
            } else if (!product.getLendStatus().equals("0")) {
                System.out.println("この商品はすでに貸出中です。");
            } else {
                // 商品情報確認画面
                while (true) {
                    System.out.println("***** 貸出商品情報確認 *****");
                    System.out.println(product.getProductId() + ":" + product.getTitle() + ":" +
                                       (product.isNew() ? "NEW" : "OLD") + ":貸出可");
                    System.out.println("1:貸出リストに登録");
                    System.out.println("2:再入力");
                    System.out.println("0:貸出処理を中断");
                    System.out.print("入力＞ ");
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // 改行を消費

                    if (choice == 1) {
                        lendList.add(product);
                        totalFee += product.isNew() ? 300 : 100; // NEW: 300円, OLD: 100円
                        System.out.println("商品が貸出リストに登録されました。");
                        break; // 次の商品入力に進む
                    } else if (choice == 2) {
                        System.out.println("再入力画面に戻ります。");
                        break; // 商品ID入力画面に戻る
                    } else if (choice == 0) {
                        System.out.println("貸出処理を中断しました。");
                        return; // メニューに戻る
                    } else {
                        System.out.println("無効な入力です。再度入力してください。");
                    }
                }
            }
        }

        // 貸出商品リスト確認画面
        System.out.println("***** 貸出商品リスト確認 *****");
        int index = 1;
        for (Goods.Product product : lendList) {
            System.out.println(index + ". " + product.getProductId() + ":" + product.getTitle() + ":" +
                               (product.isNew() ? "NEW" : "OLD"));
            index++;
        }
        System.out.println("料金合計: " + totalFee + "円");

        // 会員ID入力画面
        System.out.print("会員IDを入力してください。(戻る:0)＞ ");
        String memberId = scanner.nextLine().trim();

        if (memberId.equals("0")) {
            System.out.println("貸出処理をキャンセルしました。");
            lendList.clear(); // リストをクリア
            return; // メニューに戻る
        }

        // 会員ID確認
        if (!memberManager.isMemberValid(memberId)) {
            System.out.println("無効な会員IDです。貸出処理を中断します。");
            lendList.clear(); // リストをクリア
            return;
        }

        // 貸出処理を確定
        String today = getTodayDate();
        for (Goods.Product product : lendList) {
            goodsManager.lendProduct(product.getProductId(), memberId, today);
        }
        Goods.overwriteGoodsToCSV(); // CSVファイルに変更を反映
        System.out.println("貸出処理が完了しました。");
    }


    /* commonへ */
    private static String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}