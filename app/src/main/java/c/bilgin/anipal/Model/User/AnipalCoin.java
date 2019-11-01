package c.bilgin.anipal.Model.User;

public class AnipalCoin {
    // dunno its content!
    private int coin;
    private int realMoney;

    public AnipalCoin(){
        // default constructor
    }

    AnipalCoin(int coin){
        this.coin = coin;
        realMoney = coin/100;
    }

    public int getCoin() {
        return coin;
    }
    public int getRealMoney() {
        return realMoney;
    }
    public int spendCoin(int c){
        this.coin -= c;
        return coin;
    }
}
