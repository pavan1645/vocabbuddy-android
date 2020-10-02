# Vocabbuddy - A Flashcard based Vocab Trainer


## Contributors
* Pavankumar Mahadik (824637106)
* Pranay Mhatre (824640096)

## Project Proposal
[Vocabbuddy Proposal](https://go.aws/2xZ7Gxt)

## Description
The project would be used to develop the intended user’s vocabulary memorization ability by using flashcards. It would be divided into two sections:
1. Learning Section:
In this section, there would be a list of a few sets of words. Each set would contain 20 words displayed in a flashcard format. Each flashcard would initially contain a word, for which the user has to guess its meaning. On tapping the flashcard the actual meaning of the word would be revealed. By using the process of self-evaluation the user would then be able to add the word to the memorized list or to the words to-be-memorized list. This process goes on until all the words in a given set are completely memorized. Once all the words of a set are completely memorized a quiz section for the same set of words would be “unlocked”. The user would then have an option to continue with the learning process or attempt the quiz.
2. Quiz Section:
The quiz section would have the same sets of words each containing the same words in a random order. The user would be presented with a word and 4 meanings to choose from. If the user selects the correct meaning, a point would be added to the total score tally. This process would continue until all the quiz questions were attempted by the user. In the end, the user would be shown the total score and an option to retake the test.

## Special Instructions
Since unlocking a quiz section requires all words of that particular section to be mastered, mastering a section might take some considerable engagement time.\
For convenience, the app is initialised with a database having Section 1 completed, to give a feel how the app would look with one section completed.\
For production version, all the word lists would be incomplete at start.

## Libraries used
[Room Persistence Library ](https://developer.android.com/topic/libraries/architecture/room)

## Known Issues
There might be some Database and RecyclerView Adapter issues on API < 26, clearing data and restarting app works.\
App fully tested on API 29.