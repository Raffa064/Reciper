# Reciper

![Example](./readme-banner.gif)

A simple engine for combination games.
For a personal challenge, all the code is placed in the same file, you can see the code on [GameScreen.java](gdx-game/src/com/raffa064/reciper/screens/GameScreen.java).

## How to use
The engine works with a [rules file](gdx-game-android/assets/rules.txt) witch you can define items and combination rules for all you wish to do in the game.

An important note's that I haven't implemented any logics to put items on the screen, so you will need to edit the **show** method in [GameScreen.java](gdx-game/src/com/raffa064/reciper/screens/GameScreen.java) to add items on the screen. For tests, I have created an debug code to add items on screen...

Example:
```java
Item item = manager.createItem(itemName);
addItem(item);
```		

The rules file syntax is separated for 3 sections:
- Item declaration (Used to define the item's name and texture)
- Combination rules (Defines the recipes of the items)
- Dynamic script (Used to automate the rules with **JavaScript**)

Each section have an *tag* that defines the start of it's content:

```
#items:
...
#rules:
...
#dynamic:
...
```
After each tag you have to use the respective statement:

```
#items:
item1: texture1.png
item2: texture2.png
item3: texture3.png
```
It creates 3 items, named "item1", "item2" and "Item3", and defines each item texture.

```
#rules:
item3: item1, item2
```
Finally, it says for the "engine" that the item3 is the result of the combination for item1 and item3.

The *dynamic scripts* can do all the thing of the **"#items:"** and **"#rules:"**, bud with JavaScript:
```JavaScript
#dynamic:

//Create items
dynamic.createItem('item1', 'texture1.png')
dynamic.createItem('item2', 'texture2.png')
dynamic.createItem('item3', 'texture3.png')

//Create rule "item3 = item1 + item2"
dynamic.createRule('item3', 'item1', 'item2')
```
The advantage of the dynamic scripts is to easily automate the things... for example:

```JavaScript
#dynamic:

//Create items
for (let i = 1; i <= 3; i++) {
dynamic.createItem('item'+i, 'texture'+i+'.png')
}

//Create rule "item3 = item1 + item2"
dynamic.createRule('item3', 'item1', 'item2')
```

The **dynamic** object has 3 methods:
- **createItem**(name, texture)
- **createRule**(result, a, b)
- **getItemNames**()

**NOTE:** the *getItemNames()* returns a ```java.util.List``` of String, and you need to consider it when use, because the **Java** and the **JavaScript** has different methods and properties on the lists.

**ANOTHER NOTE:** The textures needs to be placed on the assets folder, or in a sub-directory, absolute paths aren't supported.