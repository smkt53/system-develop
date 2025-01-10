/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sysdev;

import java.util.Scanner;
import java.util.Calendar;

public class Common {
    public static Scanner scanner = new Scanner(System.in);

    /**
     * 入力値が指定された桁数以内かをチェック
     *
     * @param input 入力文字列
     * @param maxLength 最大許容文字数
     * @return 入力値が指定された桁数以内であればtrue、そうでなければfalse
     */
    public static boolean isValidLength(String input, int maxLength) {
        return input != null && input.length() <= maxLength;
    }

    /**
     * 入力値が指定された桁数と一致するかをチェック
     *
     * @param input 入力文字列
     * @param exactLength 必須の文字数
     * @return 入力値が指定された桁数と一致すればtrue、そうでなければfalse
     */
    public static boolean isExactLength(String input, int exactLength) {
        return input != null && input.length() == exactLength;
    }    

    
    public static boolean isValidDate(String year, String month, String day) {
        try {
            int y = Integer.parseInt(year);
            int m = Integer.parseInt(month);
            int d = Integer.parseInt(day);

            // 月の範囲を確認
            if (m < 1 || m > 12) {
                return false;
            }

            // 日の範囲を確認（各月の最大日数を考慮）
            Calendar calendar = Calendar.getInstance();
            calendar.setLenient(false); // 厳密な日付チェックを有効化
            calendar.set(y, m - 1, d); // 月は0から始まるため、-1
            calendar.getTime(); // 日付が無効の場合、ここで例外がスローされる
            return true;
        } catch (Exception e) {
            return false; // 数値変換エラーや例外が発生した場合は無効
        }
    }    
    /**
     * 8桁のYYYYMMDD形式の日付を受け取って有効性をチェック
     *
     * @param dateStr YYYYMMDD形式の日付文字列
     * @return 有効な日付ならtrue、無効ならfalse
     */
    public static boolean isValidDate8(String dateStr) {
        if (dateStr == null || dateStr.length() != 8) {
            return false; // nullまたは8桁でない場合は無効
        }

        try {
            // 年、月、日を分割
            String year = dateStr.substring(0, 4);
            String month = dateStr.substring(4, 6);
            String day = dateStr.substring(6, 8);

            // 個別の年、月、日チェックを再利用
            return isValidDate(year, month, day);
        } catch (Exception e) {
            return false; // 文字列の切り出しでエラーが発生した場合も無効
        }
    }    
}
