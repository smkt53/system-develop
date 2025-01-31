package com.mycompany.sysdev;

import java.io.IOException;
import java.util.Scanner;


public class Menu {
    private static Scanner scanner = new Scanner(System.in);
    private static Goods goods = new Goods();
    private static MemberManager memberManager = new MemberManager();
    private static String memberIds;
    

    public static void main(String[] args) {
        System.out.println("商品情報を読み込んでいます...");
        goods.loadGoodsFromCSV(); // 商品情報の読み込み
        System.out.println("読み込み完了。登録されている商品数: " + goods.getGoodsList().size());
        mainMenu();
    }

    public static void mainMenu() {
        while (true) {
            
            System.out.println("商品情報を読み込んでいます...");
            goods.loadGoodsFromCSV(); // 商品情報の読み込み
            System.out.println("読み込み完了。登録されている商品数: " + goods.getGoodsList().size());   
            
            System.out.println("***** スタッフメインメニュー *****");
            System.out.println("1: レンタルメニュー");
            System.out.println("2: 会員情報管理メニュー");
            System.out.println("3: 商品情報管理メニュー");
            System.out.println("4: おすすめ検索メニュー");
            System.out.println("0: 終了");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            switch (choice) {
                case 1:
                    rentalMenu();
                    break;
                case 2:
                    memberManagementMenu(); // 会員情報管理メニューへ
                    break;
                case 3:
                    goods.goodsManageMenu(scanner); // 商品情報管理メニューへ
                    break;
                case 4:
                    Reco.Recommend();
                    break;
                case 0:
                    if (confirmExit()) {
                        System.out.println("プログラムを終了します。");
                        return;
                    } else {
                        System.out.println("メインメニューに戻ります。");
                    }
                    break;
                default:
                    System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }
    
    private static void rentalMenu() {
        while (true) {
            System.out.println("***** レンタルメニュー *****");
            System.out.println("1: 貸出");
            System.out.println("2: 返却");
            System.out.println("0: スタッフメインメニューに戻る");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    Lend.lendMenu();
                    break;
                case 2:
                    Ret.retMenu();
                    break;
                case 0:
                    return; // メインメニューに戻る
                default:
                    System.out.println("無効な入力です。再度入力してください。");
            }
        }   
    }
    /*
     * 関数名            : memberManagementMenu
     * 機能              : 【D-1120】会員情報管理メニューを表示し、ユーザーの選択に基づき会員の登録、変更、削除の処理を行う
     * 入力パラメータ    : なし
     * 出力パラメータ    : なし
     * 戻り値            : void
     * 特記事項          : 
     *                      - ユーザーが入力した選択肢に応じて、registerMemberFlow または modifyOrDeleteMemberFlow を呼び出す。
     *                      - 無効な入力があった場合、再度入力を促す。
     *                      - "0"を選択すると、スタッフメインメニューに戻る。
     */
    private static void memberManagementMenu() {
        while (true) {
            System.out.println("***** 会員情報管理メニュー *****");
            System.out.println("1: 会員情報確認・変更・削除");
            System.out.println("2: 会員登録");
            System.out.println("0: スタッフメインメニューに戻る");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費

            switch (choice) {
                case 1:
                    modifyOrDeleteMemberFlow();
                    break;
                case 2:
                    registerMemberFlow();
                    break;
                case 0:
                    return; // メインメニューに戻る
                default:
                    System.out.println("無効な入力です。再度入力してください。");
            }
        }
    }
    /*
     * 関数名            : registerMemberFlow
     * 機能              : 【D-1122】新規会員登録のためのフローを管理し、ユーザーの入力に基づいて会員情報の入力および確認を行う
     * 入力パラメータ    : なし
     * 出力パラメータ    : なし
     * 戻り値            : void
     * 特記事項          : 
     *                      - ユーザーが情報入力を選択した場合、inputNewMember メソッドを呼び出して新規会員情報を取得する。
     *                      - 新規会員情報が正しく入力された場合、confirmAndRegisterMember メソッドを通じて会員を登録する。
     *                      - "0"を選択すると、会員情報管理メニューに戻る。
     */
    private static void registerMemberFlow() {
        Member member = inputNewMember();
        if (member != null) {
            confirmAndRegisterMember(member);
        }
    }
    /*
     * 関数名            : inputNewMember
     * 機能              : 【D-1122】ユーザーから新規会員情報を入力として受け取り、Member オブジェクトを生成する
     * 入力パラメータ    : なし
     * 出力パラメータ    : なし
     * 戻り値            : Member : 入力された情報に基づいて生成された新規会員オブジェクト
     * 特記事項          : 
     *                      - ユーザーから名前、住所、電話番号、生年月日を順に入力として受け取る。
     *                      - 生年月日は "yyyyMMdd" 形式でフォーマットされる。
     *                      - 会員IDは "99" + 現在の日付（"yyyyMMdd"） + 当日の会員数に基づく2桁の連番で生成される。
     *                      - 会員IDは addMemberをするときに生成されるためここでは追加しない 
     *                      - 入力された電話番号や生年月日に対するバリデーションは行われていないため、必要に応じて追加すること。
     */   
    private static Member inputNewMember() {
        while(true){
            System.out.println("***** 新規会員情報入力 *****");
            System.out.println("新規会員情報を入力してください。");
            System.out.print("名前＞ ");
            String name = scanner.nextLine();
            System.out.print("住所＞ ");
            String address = scanner.nextLine();
            System.out.print("電話番号＞ ");
            String phoneNumber = scanner.nextLine();
            System.out.print("生年月日（yyyyMMdd）＞");
            String birthDate = scanner.nextLine();

            System.out.println("**********************");
            System.out.println("1: 確定");
            System.out.println("0: 会員情報管理メニューに戻る");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費
            switch (choice) {
                case 1:
                    // 電話番号が11桁であるか確認する
                    if ((phoneNumber.length() != 11)&(phoneNumber.length() != 10)) {
                        System.out.println("電話番号は11桁もしくは11桁で入力してください。");
                        continue;
                    }
                    // 生年月日が8桁であるか確認する
                    if (birthDate.length() != 8) {
                        System.out.println("生年月日は8桁で入力してください。");
                        continue;
                    }
                    return new Member("", name, address, phoneNumber, birthDate);
                case 0:
                    return null; // 会員情報管理メニューに戻る
                default:
                    System.out.println("無効な入力です。再度入力してください。");
            }
        }
        
    }

    /*
     * 関数名            : confirmAndRegisterMember
     * 機能              : 【D-1123】入力された新規会員情報を確認し、ユーザーの承認に基づいて会員を登録する
     * 入力パラメータ    : 
     *                      Member member : 登録を確認および実行する対象の会員オブジェクト
     * 出力パラメータ    : なし
     * 戻り値            : void
     * 特記事項          : 
     *                      - ユーザーが会員情報の登録を承認した場合、MemberManager の addMember メソッドを呼び出して会員を登録する。
     *                      - 登録後、スタッフメインメニューに戻る。
     *                      - ユーザーがキャンセルを選択した場合、登録フローを中断し、新規会員情報入力画面に戻る。
     *                      - 会員情報の保存中に IOException が発生した場合、エラーメッセージを表示する。
     */
    private static void confirmAndRegisterMember(Member member) {
        outloop:
        while(true){
            System.out.println("***** 新規会員情報確認 *****");
            System.out.println("以下の情報で登録しますか？");
            System.out.println("名前: " + member.getName());
            System.out.println("住所: " + member.getAddress());
            System.out.println("電話番号: " + member.getPhone());
            System.out.println("生年月日: " + member.getBirthDate());

            System.out.println("**********************");
            System.out.println("1: 確定");
            System.out.println("2: 変更");
            System.out.print("入力＞ ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 改行を消費
            switch (choice) {
                case 1:
                    String memberId = memberManager.addMember(member.getName(), member.getAddress(), member.getPhone(), member.getBirthDate());
                    if (memberId != null) {
                        System.out.println("会員ID: " + memberId);
                    } else {
                        System.out.println("会員情報の保存中にエラーが発生しました。");
                    }
                    break outloop;
                case 2:
                    inputNewMember();
                    break outloop;
                default:
                    System.out.println("無効な入力です。最初から入力してください。");
                }
        }
    }
    /*
     * 関数名            : modifyOrDeleteMemberFlow
     * 機能              : 【D-1121, 1123】指定された会員IDに基づき、会員情報の変更または削除のフローを管理する
     * 入力パラメータ    : なし
     * 出力パラメータ    : なし
     * 戻り値            : void
     * 特記事項          : 
     *                      - ユーザーが "0" を入力した場合、会員情報管理メニューに戻る。
     *                      - 指定された会員IDが存在しない場合、エラーメッセージを表示し、再入力を促す。
     *                      - 会員情報が確認された後、ユーザーは情報の変更または削除を選択できる。
     *                      - 選択に応じて、modifyMember または deleteMember メソッドを呼び出す。
     *                      - 操作完了後、スタッフメインメニューに戻る。
     */
    private static void modifyOrDeleteMemberFlow() {
        //D-1121
        String memberId = null;
        int choice = 1;
        if(memberIds == null){
            outloop:
            while(true){
                System.out.println("***** 会員ID入力 *****");
                System.out.print("会員IDを入力してください＞ ");
                memberId = scanner.nextLine();
        
                //D-1124
                System.out.println("**********************");
                System.out.println("1: 確定");
                System.out.println("0: 会員情報管理メニューに戻る");
                System.out.print("入力＞ ");
                int choice2 = scanner.nextInt();
                scanner.nextLine(); // 改行を消費
                switch (choice2) {
                    case 1:
                        if (!memberManager.isMemberValid(memberId)) {
                            System.out.println("会員IDが見つかりません。再度入力してください。");
                            continue;
                        }
                        break outloop;  //確定します 
                    case 0:
                        return; // 会員情報管理メニューに戻る
                    default:
                        System.out.println("無効な入力です。再度入力してください。");
                }
            }             
        }else {
            memberId = memberIds;
        }
        

        //D-1125
        
        Member member = memberManager.searchMemberById(memberId);
        System.out.println("***************会員情報***************");
        System.out.println("会員ID: " + member.getId());
        System.out.println("名前: " + member.getName());
        System.out.println("住所: " + member.getAddress());
        System.out.println("電話番号: " + member.getPhone());
        System.out.println("生年月日: " + member.getBirthDate());
        
        if(memberIds == null){
            System.out.println("**********************");
            System.out.println("1: 会員情報変更・削除");
            System.out.println("0: 会員情報管理メニューに戻る");
            System.out.print("入力＞ ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
        }
        memberIds = memberId;

        
        switch (choice) {
            case 1:
                System.out.println("***** 会員情報変更・削除 *****");
                System.out.println("0: 会員情報管理メニューに戻る");
                System.out.println("1: 会員情報変更");
                System.out.println("2: 会員削除");
                System.out.print("入力＞ ");
                int choice2 = scanner.nextInt();
                scanner.nextLine(); // consume newline
                switch (choice2) {
                    case 0:
                        memberIds = null;
                        return; //会員情報管理メニューに戻る
                    case 1:
                        modifyMember(member);
                        break;
                    case 2:
                        deleteMember(member);
                        break;
                    default:
                        memberIds = null;
                        System.out.println("無効な入力です。再度入力してください。");
                }
                break;
            case 0:
                memberIds = null;
                return; // メインメニューに戻る
            default:
                memberIds = null;
                System.out.println("無効な入力です。再度入力してください。");
        }
    }
    /*
     * 関数名            : modifyMember
     * 機能              : 【D-1125】指定された会員の情報を変更し、更新内容を保存する
     * 入力パラメータ    : 
     *                      Member member : 変更対象の会員オブジェクト
     * 出力パラメータ    : なし
     * 戻り値            : void
     * 特記事項          : 
     *                      - ユーザーが情報変更を選択した場合、各項目の新しい値を入力として受け取る。
     *                      - 未入力の項目は変更されない。
     *                      - 変更内容の確認後、MemberManager の saveMembers メソッドを呼び出して変更を保存する。
     *                      - 保存に成功した場合、会員情報管理メインメニューに戻る。
     *                      - 保存中に IOException が発生した場合、エラーメッセージを表示する。
     */
    private static void modifyMember(Member member) {
        //D-1126
        System.out.println("***** 会員情報変更内容入力 *****");
        System.out.println("会員情報を変更します。");
        System.out.println("会員ID: " + member.getId());
        System.out.println("名前: " + member.getName());
        System.out.println("住所: " + member.getAddress());
        System.out.println("電話番号: " + member.getPhone());
        System.out.println("生年月日: " + member.getBirthDate());
        System.out.println("変更内容を入力してください。未入力の項目は変更されません。");
        System.out.print("名前＞ ");
        String name = scanner.nextLine();
        System.out.print("住所＞ ");
        String address = scanner.nextLine();
        System.out.print("電話番号＞ ");
        String phoneNumber = scanner.nextLine();
        System.out.print("生年月日（yyyyMMdd）＞");
        String birthDate = scanner.nextLine();
        birthDate = formatBirthDate(birthDate);

        //D-1128
        System.out.println("***** 会員情報変更内容確認 *****");
        System.out.println("以下の情報に変更します。");
        System.out.println("名前: " + name);
        System.out.println("住所: " + address);
        System.out.println("電話番号: " + phoneNumber);
        System.out.println("生年月日: " + birthDate);
        if(name.equals("")){
            name = member.getName();
        }
        if(address.equals("")){
            address = member.getAddress();
        }
        if(phoneNumber.equals("")){
            phoneNumber = member.getPhone();
        }
        if(birthDate.equals("")){
            birthDate = member.getBirthDate();
        }
        System.out.println("**********************");
        System.out.print("変更しますか？(Y / N)＞ ");
        String input = scanner.nextLine().trim().toUpperCase();
        if (input.equals("Y")) {
            try {
                memberManager.deleteAndAddMember(member.getId(), name, address, phoneNumber, birthDate);
                System.out.println("会員情報を変更しました。" + member.getId());
            } catch (Exception e) {
                System.out.println("会員情報の保存中にエラーが発生しました。");
            }
            memberIds = null;
        } else {
            System.out.println("会員情報の変更をキャンセルしました。");
            modifyOrDeleteMemberFlow();
        }
        

    }
    /*
     * 関数名            : deleteMember
     * 機能              : 【D-1127】指定された会員を削除し、CSVファイルを更新する
     * 入力パラメータ    : 
     *                      Member member : 削除対象の会員オブジェクト
     * 出力パラメータ    : なし
     * 戻り値            : void
     * 特記事項          : 
     *                      - ユーザーの確認に基づき、会員の削除を実行する。
     *                      - 削除が成功した場合、スタッフメインメニューに戻る。
     *                      - 削除が失敗した場合、エラーメッセージを表示する。
     *                      - 削除操作後、MemberManager の deleteMemberById メソッドを呼び出してCSVファイルを更新する。
     */
    private static void deleteMember(Member member) {
        System.out.println("***** 会員情報削除確認 *****");

        System.out.println("以下の会員を削除します。");
        System.out.println("会員ID: " + member.getId());
        System.out.println("名前: " + member.getName());
        System.out.println("住所: " + member.getAddress());
        System.out.println("電話番号: " + member.getPhone());
        System.out.println("生年月日: " + member.getBirthDate());
        System.out.print("削除しますか？(Y / N)＞ ");
        String input = scanner.nextLine().trim().toUpperCase();
        if (input.equals("Y")) {
            if (memberManager.deleteMemberById(member.getId())) {
                System.out.println("会員を削除しました。");
                try {
                    memberManager.saveMembers();
                } catch (IOException e) {
                    System.out.println("会員情報の保存中にエラーが発生しました。");
                }
            } else {
                System.out.println("会員の削除に失敗しました。");
            }
            memberIds = null;
        } else {
            System.out.println("会員の削除をキャンセルしました。");
            modifyOrDeleteMemberFlow();
        }
    }

    private static String formatBirthDate(String birthDate) {
        if (birthDate.length() == 8) {
            return birthDate.substring(0, 4) + "/" + birthDate.substring(4, 6) + "/" + birthDate.substring(6, 8);
        }
        return birthDate;
    }

    private static boolean confirmExit() {
        while (true) {
            System.out.print("プログラムを終了してもよろしいですか？(Y / N)＞ ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                return true;
            } else if (input.equals("N")) {
                return false;
            } else {
                System.out.println("無効な入力です。'Y' または 'N' を入力してください。");
            }
        }
    }
}
