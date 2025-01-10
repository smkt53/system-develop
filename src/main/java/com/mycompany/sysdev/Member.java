/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sysdev;


import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Member {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String birthDate;

    // Constructors
    public Member(String id, String name, String address, String phone, String birthDate) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.birthDate = birthDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    // Override toString for easy CSV export
    @Override
    public String toString() {
        return String.join(",", id, name, address, phone, birthDate);
    }

    // Static method for creating a Member from a CSV line
    public static Member fromCsv(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid CSV format for Member: " + csvLine);
        }
        return new Member(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}

class MemberManager {
    private List<Member> members;
    private static final String FILE_NAME = "member.csv";

    // Constructor
    public MemberManager() {
        this.members = new ArrayList<>();
        try {
            this.loadMembers(); // Load existing members at initialization
        } catch (IOException e) {
            System.out.println("初期化時に会員情報の読み込みに失敗しました: " + e.getMessage());
        }
    }

   /*
    * 関数名            : loadMembers
    * 機能              : CSVファイル "member.csv" から会員情報を読み込み、members リストを更新する
    * 入力パラメータ    : なし
    * 出力パラメータ    : なし
    * 戻り値            : void
    * 特記事項          : 
    *                      - ファイル読み込み中に不正なレコードが存在する場合、そのレコードはスキップされる。
    *                      - メソッド実行前に members リストはクリアされ、新しいデータで再構築される。
    *                      - IOException が発生する可能性があるため、呼び出し元で適切にハンドリングする必要がある。
    */
    public void loadMembers() throws IOException {
        this.members.clear(); // Clear existing members
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Member member = Member.fromCsv(line);
                    this.members.add(member);
                } catch (IllegalArgumentException e) {
                    System.out.println("不正な会員情報をスキップしました: " + e.getMessage());
                }
            }
        }
    }
    
    public boolean isMemberValid(String memberId) {
        return members.stream().anyMatch(member -> member.getId().equals(memberId));
    }        

   /*
    * 関数名            : saveMembers
    * 機能              : 現在のmembersリストの内容をCSVファイル "member.csv" に保存する
    * 入力パラメータ    : なし
    * 出力パラメータ    : なし
    * 戻り値            : void
    * 特記事項          : 
    *                      - メソッド実行時に既存の "member.csv" ファイルは上書きされる。
    *                      - IOException が発生する可能性があるため、呼び出し元で適切にハンドリングする必要がある。
    *                      - membersリストの各MemberオブジェクトはtoString() メソッドでCSV形式に変換される。
    */
    public void saveMembers() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Member member : this.members) {
                writer.write(member.toString());
                writer.newLine();
            }
        }
    }

    /*
     * 関数名            : addMember
     * 機能              : 新規会員を追加し、会員IDを生成して保存する
     * 入力パラメータ    : 
     *                      String name     : 会員の名前
     *                      String address  : 会員の住所
     *                      String phone    : 会員の電話番号（'xxx-xxxx-xxxx'の形式）
     *                      String birthDate: 会員の生年月日
     * 出力パラメータ    : なし
     * 戻り値            : String : 新規に取得した会員ID（成功時） / null（失敗時）
     * 特記事項          : 
     *                      - 電話番号は13桁の形式（'xxx-xxxx-xxxx'）である必要がある。
     *                      - 会員IDは "99" + 日付（yyyyMMdd） + 2桁の連番で、合計12桁となる。
     *                      - 会員を追加後、CSVファイルに即時保存される。
     *                      - 名前、住所、生年月日は適切に入力されている必要がある。
     */
    public String addMember(String name, String address, String phone, String birthDate) {
        // Generate new member ID
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String memberId = generateMemberId(today);
        if (memberId == null) {
            return null;
        }

        // Create new member
        Member newMember = new Member(memberId, name, address, phone, birthDate);
        this.members.add(newMember);

        // Save members to file
        try {
            this.saveMembers();
            System.out.println("会員を追加しました: " + newMember);
            return memberId;
        } catch (IOException e) {
            System.out.println("会員情報の保存中にエラーが発生しました: " + e.getMessage());
            return null;
        }

    }
    /*
     * 関数名            : generateMemberId
     * 機能              : 新規の会員IDを生成する
     * 入力パラメータ    : 
     *                      String today : 現在の日付（"yyyyMMdd"形式）
     * 出力パラメータ    : なし
     * 戻り値            : String : 生成された会員ID
     * 特記事項          : 
     *                      - 会員IDは "99" + 日付（yyyyMMdd） + 2桁の連番で、合計12桁となる。
     *                      - メソッド実行時に最新の会員データを読み込むために loadMembers メソッドを呼び出す。
     *                      - 同一日付の会員IDが既に存在する場合、連番がインクリメントされる。
     *                      - IOException が発生した場合、エラーメッセージをコンソールに出力する。
     *                      - 生成された会員IDはコンソールにログとして出力される。
     */
    String generateMemberId(String today) {
        String memberId = "99" + today + String.format("%02d", getMembersCount(today));
        System.out.println("新規会員IDを生成しました: " + memberId);
        return memberId;
    }

    /*
     * 関数名            : searchMemberById
     * 機能              : 会員IDを使用してcsvファイルから特定の会員を検索する
     * 入力パラメータ    : 
     *                      String id    : 検索対象の会員ID
     * 出力パラメータ    : なし
     * 戻り値            : Member    : 該当する会員情報（存在する場合）
     *                       null      : 該当する会員が存在しない場合
     * 特記事項          : 
     *                      - 会員IDがリスト内で一意であることを前提としている。
     *                      - 大文字小文字は区別される（必要に応じて調整可能）。
    */
    public Member searchMemberById(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Member member = Member.fromCsv(line);
                    if (member.getId().equals(id)) {
                        return member;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("不正な会員情報をスキップしました: " + e.getMessage());
                    return null;
                }
            }
        } catch (IOException e) {
            System.out.println("初期化時に会員情報の読み込みに失敗しました: " + e.getMessage());
            return null;
        }
        return null;
    }

    /*
     * 関数名            : deleteMemberById
     * 機能              : 【D-XXXX】会員IDを使用して特定の会員を削除する
     * 入力パラメータ    : 
     *                      String id    : 削除対象の会員ID
     * 出力パラメータ    : なし
     * 戻り値            : boolean    : TRUE（削除成功）/ FALSE（削除失敗または会員IDが存在しない）
     * 特記事項          : 
     *                      - CSVファイルに即時保存される。
     *                      - 会員IDがリスト内で一意であることを前提としている。
     *                      - CSVファイル保存中にIOExceptionが発生する可能性がある。
     *                      - 削除成功時にはコンソールに確認メッセージが表示される。
     */
    public boolean deleteMemberById() {
        // ユーザーに会員IDを入力してもらう
        System.out.println("削除する会員の会員IDを入力してください。");
        String memberId = System.console().readLine();
        // 会員IDが存在するか確認
        if (!isMemberValid(memberId)) {
            System.out.println("会員IDが存在しません。");
            return false;
        }
        // 会員IDに対応する会員情報を取得
        Member member = searchMemberById(memberId);
        if (member == null) {
            System.out.println("会員IDに対応する会員情報が見つかりませんでした。");
            return false;
        }
        // 会員情報を削除
        this.members.remove(member);
        // CSVファイルに保存
        try {
            this.saveMembers();
            System.out.println("会員情報を削除しました: " + member);
            return true;
        } catch (IOException e) {
            System.out.println("会員情報の保存中にエラーが発生しました: " + e.getMessage());
            return false;
        }
    }

    // Get all members
    public List<Member> getMembers() {
        return new ArrayList<>(this.members); // Return a copy of the members list
    }

    // Get the count of members
    public int getMembersCount(String date) {
        // "99YYYYMMDD" 形式で始まる会員IDの数をカウント
        return (int) this.members.stream()
            .filter(member -> member.getId().startsWith("99" + date))
            .count();
    }
}
