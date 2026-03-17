package studioyes.kelimedunyasi.model;

import com.badlogic.gdx.math.MathUtils;
import studioyes.kelimedunyasi.managers.LanguageManager;

public class PetQuest {

    public enum QuestType {
        FIND_LONG_WORDS,      // Find words with certain length
        FIND_BONUS_WORDS,     // Find bonus words
        FIND_WORDS_WITH_LETTER // Find words containing a specific letter
    }

    private QuestType type;
    private int targetAmount;
    private int currentAmount;
    private char targetLetter;
    private int targetLength;
    private boolean completed;
    private int coinReward = 25;
    private int xpReward = 50;

    public PetQuest(QuestType type, int targetAmount) {
        this.type = type;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
        this.completed = false;
    }

    public static PetQuest generateRandomQuest(int levelIndex) {
        int typeIndex = MathUtils.random(2);
        PetQuest quest;
        
        switch (typeIndex) {
            case 0: // FIND_LONG_WORDS
                quest = new PetQuest(QuestType.FIND_LONG_WORDS, MathUtils.random(1, 2));
                quest.targetLength = 5; // Can be adjusted based on level difficulty
                break;
            case 1: // FIND_BONUS_WORDS
                quest = new PetQuest(QuestType.FIND_BONUS_WORDS, MathUtils.random(1, 2));
                break;
            case 2: // FIND_WORDS_WITH_LETTER
            default:
                quest = new PetQuest(QuestType.FIND_WORDS_WITH_LETTER, MathUtils.random(1, 2));
                // Pick a common letter for the level might be better, but let's start simple
                quest.targetLetter = 'A'; 
                break;
        }
        return quest;
    }

    public String getQuestDescription() {
        switch (type) {
            case FIND_LONG_WORDS:
                return LanguageManager.format("quest_long_words", targetAmount, targetLength);
            case FIND_BONUS_WORDS:
                return LanguageManager.format("quest_bonus_words", targetAmount);
            case FIND_WORDS_WITH_LETTER:
                return LanguageManager.format("quest_words_with_letter", targetAmount, String.valueOf(targetLetter));
            default:
                return "";
        }
    }

    public boolean progress(String word, boolean isBonus) {
        if (completed) return false;

        boolean advanced = false;
        switch (type) {
            case FIND_LONG_WORDS:
                if (!isBonus && word.length() >= targetLength) {
                    currentAmount++;
                    advanced = true;
                }
                break;
            case FIND_BONUS_WORDS:
                if (isBonus) {
                    currentAmount++;
                    advanced = true;
                }
                break;
            case FIND_WORDS_WITH_LETTER:
                if (!isBonus && word.toUpperCase().contains(String.valueOf(targetLetter).toUpperCase())) {
                    currentAmount++;
                    advanced = true;
                }
                break;
        }

        if (currentAmount >= targetAmount) {
            completed = true;
        }
        
        return advanced;
    }

    public QuestType getType() { return type; }
    public int getTargetAmount() { return targetAmount; }
    public int getCurrentAmount() { return currentAmount; }
    public boolean isCompleted() { return completed; }
    public int getCoinReward() { return coinReward; }
    public int getXpReward() { return xpReward; }
}
