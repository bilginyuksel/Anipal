package c.bilgin.anipal.Model.User;

public class AnipalUserLevel{
        private AnipalLevel anipalLevel;
        private int anipalLevelBar;


        public AnipalUserLevel(){
                // first initialization . when user created.
                // or write a method for user creation only.
                anipalLevel = AnipalLevel.LEVEL1;
                anipalLevelBar = 0;
        }

        public AnipalUserLevel(AnipalLevel anipalLevel,int anipalLevelBar){
                // or only store int anipalLevelBar information on db.
                // and calculate the level here.
                this.anipalLevelBar = anipalLevelBar;
                this.anipalLevel = anipalLevel;
        }

        public AnipalLevel getAnipalLevel() {
                return anipalLevel;
        }

        public int getAnipalLevelBar() {
                return anipalLevelBar;
        }

}

