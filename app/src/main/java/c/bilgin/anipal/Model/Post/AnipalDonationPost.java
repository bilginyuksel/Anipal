package c.bilgin.anipal.Model.Post;

import java.util.HashMap;

public class AnipalDonationPost extends AnipalAbstractPost{


    private String donationPurpose;
    private int donationPrice;
    // User UUID and how much is he donated ?
    private HashMap<String,Integer> donators;
    private int currentDonation;

    public AnipalDonationPost(){

    }

    public AnipalDonationPost(String userUUID,String donationPurpose,int donationPrice) {
        super(userUUID);
        // Create a new post
        this.donators = new HashMap<>();
        this.donationPurpose = donationPurpose;
        this.donationPrice = donationPrice;
        this.currentDonation = 0;
    }

    public AnipalDonationPost(AnipalAbstractPost post) {
        // Get a post which it exists.
        super(post);
        this.donators = ((AnipalDonationPost)post).donators;
        this.donationPrice = ((AnipalDonationPost) post).donationPrice;
        this.donationPurpose = ((AnipalDonationPost) post).donationPurpose;
        this.currentDonation = ((AnipalDonationPost) post).currentDonation;
    }

    @Override
    public int getListItemType() {
        return ListItem.TYPE_DONATION;
    }

    // get Donation from donators
    public AnipalDonationPost getDonation(String userUUID,int price){
        if(donators==null) donators = new HashMap<>();
        donators.put(userUUID,price);
        currentDonation += price;
        return this;
    }

    public int getDonationPrice() {
        return donationPrice;
    }
    public String getDonationPurpose() {
        return donationPurpose;
    }
    public int getCurrentDonation() {
        return currentDonation;
    }
    public HashMap<String, Integer> getDonators() {
        return donators;
    }
}
