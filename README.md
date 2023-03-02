
# Inventory Library

Inventory Library is a library that has to be run as a standalone plugin.
Using this library you can use extended menus, so the player's inventory slots can be used for your custom item placement with your own click actions.

The library is still unfinished, there are missing features like:
- MenuHistory
- CustomInventoryCloseEvent
- Menu class (Used as a template for RenderedMenu)
    - will be used soon, so you do not have to create RenderedMenu manually
- More RenderedButton constructor variants


## Creating a RenderedMenu

- ### Casual chest inventory
    ```java
    var renderedMenu = new RenderedMenu(54, "Your Title");
    ```
    Using this code we create the RenderedMenu, which works as a menu session.
- ### Other inventory types
    If you want to create Anvil/Beacon/Hopper/... inventories, you can use the construtor that contains the InvetoryType enum parameter.
    ```java
    var renderedMenu = new RenderedMenu(InventoryType.HOPPER, "Your Title");
    ```

## Adding buttons to your invetory
- ### Without template
    ```java
    var renderedMenu = ... your rendered menu instance;

    var slotSelection = new SlotSelection(0);
    var itemStack = new ItemStack(Material.STONE);

    var renderedButton = new RenderedButton(itemStack, slotSelection, event -> {
        if (event.getClickType() == SWAP) {
            Bukkit.broadcastMessage("You have tried to swap the item!");
            event.setCancelled(true);
        } else if (event.getClickType() == LEFT_CLICK) {
            Bukkit.broadcastMessage("You have just left clicked!");
            event.setCancelled(true);
        }
    });

    renderedMenu.addComponent(renderedButton);
    ```
- ### With template
    ```java
    var renderedMenu = ... your rendered menu instance;

    var slotSelection = new SlotSelection(0);
    var itemStack = new ItemStack(Material.STONE);

    var button = new Button(itemStack, slotSelection event -> {
        if (event.getClickType() == SWAP) {
            Bukkit.broadcastMessage("You have tried to swap the item!");
            event.setCancelled(true);
        } else if (event.getClickType() == LEFT_CLICK) {
            Bukkit.broadcastMessage("You have just left clicked!");
            event.setCancelled(true);
        }
    });

    var renderedButton = new RenderedButton(button);

    renderedMenu.addComponent(renderedButton);
    ```
    At the moment there is really no advantage of using the Button class. It will be all implemented later then!
## Click Types
- LEFT_CLICK
- SHIFT_LEFT_CLICK
- RIGHT_CLICK
- SHIFT_RIGHT_CLICK
- SWAP
- CLONE
- THROW
- QUICK_CRAFT
- PICKUP_ALL
- NUM_1
- NUM_2
- NUM_3
- NUM_4
- NUM_5
- NUM_6
- NUM_7
- NUM_8
- NUM_9

