# CSCI-310-Project-2
***PLEASE ACTIVELY EDIT THIS FILE TO DOCUMENT YOUR PROGRESS and UPDATE frequently to stay up-to-date with commits***
## Status/Progress of Features
### Feature 1 User Setup & Pet Creation: Devin (DT)


### Feature 2 Chat with your pet: David (DZ)


### Feature 3 Feeding & Care: Chanwoo (CL)


### Feature 4 Pet Growth Levels: Andy (AZ)


### Feature 5 Journal: Li Yuan (LYH)
- LLM was set up so far
- kinda got llm working with journal variables/functions
- kinda got entries to be able to display with search bar

## Latest Updates on Commits
10/26 LYH: updated journal activity to get UI with entries with RecycleView & got rid of journal-related functions within LLM client file  
10/22 LYH: Set up the LLM inference for the journal

## TA Notes on Implementation:
1. How many animals do you want as pet options? Which animals? These are our ideas: Fish, cat, dog, dragon, hamster

Two pet options is the minimum requirement.  No particular animals are preferred.  If you want to go LLM route, I'd assume that "well-known animals" would be ideal for pet options (e.g., cat and dog).

2. How should the product respond to irrelevant/inappropriate prompts?

While that's great to think about (and should be mentioned in your project requirements documentation if you'd like), I do not plan to grade based on whether "safe guardrails" are implemented.  Furthermore, users shouldn't be able to "prompt" the pet to respond.  Rather, my assumption is that you will have fixed prompts to LLMs that will be triggered at certain points of interacting with the app.

3. What happens if we overfeed/over-chat with the pet?

I suggest that you do not allow further actions if the relevant meters are full.  e.g., If the pet is full, do not allow feeding; if the pet is happy (max), do not allow chatting; if the pet is energized (max), do not allow resting / tucking in.

4. Is there anything that happens if the pet is left alone for too long (user does not log in)? How is time a factor in the game?

Meters will decrease as time passes.  While in reality, a game may do this over the course of hours or even days, for our project's debugging and demonstration purposes, you can do this in seconds or minutes.

5. Can you explain the “leveling up”? What does that mean for the LLM to evolve in its personality and responses? How many possible levels are there?

Similarly to #4, time will also contribute to leveling up.  If the meters are full and some time has passed since the last "level up" (again, you can decide how long this should take realistically for demo purposes), a pet should be able to level up.  Easiest to "evolve LLM's personality and responses" would be to associate levels with maturity.  I could imagine that you associate level 1 as infant, level 2 as toddler, level 3 as teenager, level 4 as young adult, and level 5 as adult, and you prompt LLM (or have some mapping of a response) that takes account of this information in the chat response or diary entry.  At least 3 levels are required.

6. Are there any UI basic requirements?

Please have some meters visually present, as well as something that represents the selected pet.  In terms of UI that corresponds to different functionalities, that is up to the individual developing each feature.

7. What are the security/privacy expectations? (user data)

Similar to #2, this is great to think about in the requirements phase (and do mention it in your doc if applicable), but for the purposes of the project implementation, you do not need to worry about it.  Any database framework you end up using (e.g., Google Firebase) should be fine for storing user data.

8. Will the user be able to edit the journal entries, or is it simply being generated? Also, are the journal entries automatically generated each day, or is there a button or something to press to generate it?

No edits are needed, they are simply being generated.  Journal entries should be generated on the days where the user logs in and interacts with the pet somehow.  You can decide if this will be done automatically or only when the user presses a button or something.

