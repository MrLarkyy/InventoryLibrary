package xyz.larkyy.inventorylibrary.api.ui.pagination;

import org.bukkit.entity.Player;
import xyz.larkyy.inventorylibrary.api.ui.event.CustomInventoryClickEvent;
import xyz.larkyy.inventorylibrary.api.ui.flag.InventoryFlag;
import xyz.larkyy.inventorylibrary.api.ui.rendered.RenderedMenu;
import xyz.larkyy.inventorylibrary.api.ui.rendered.component.RenderedButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PaginationManager {

    private final List<RenderedMenu> pages;
    private final RenderedButton nextPageComponent;
    private final RenderedButton prevPageComponent;
    private final Player player;
    private boolean showDummyPageBtns = true;
    private int page = 0;

    public PaginationManager(Player player, List<RenderedMenu> pages, RenderedButton nextPageComponent, RenderedButton prevPageComponent) {
        this.player = player;
        this.pages = new ArrayList<>(pages);
        this.nextPageComponent = nextPageComponent;
        this.prevPageComponent = prevPageComponent;
    }

    public PaginationManager(Player player, RenderedButton nextPageComponent, RenderedButton prevPageComponent) {
        this(player,new ArrayList<>(),nextPageComponent,prevPageComponent);
    }

    public void setShowDummyPageBtns(boolean showDummyPageBtns) {
        this.showDummyPageBtns = showDummyPageBtns;
    }

    public void addPage(RenderedMenu page) {
        this.pages.add(page);
    }

    public void removePage(RenderedMenu page) {
        this.pages.remove(page);
    }

    public void removePage(int page) {
        if (page >= pages.size()) {
            return;
        }
        this.pages.remove(page);
    }

    public int getPage() {
        return page;
    }

    public void open() {
        open(page);
    }

    public void open(int page) {
        if (page >= pages.size()) {
            return;
        }
        var prevPage = pages.get(page);
        var prevPageFlags = prevPage.getFlags();
        boolean hadFlag = false;
        if (prevPageFlags.contains(InventoryFlag.CLEAR_HISTORY_ON_CLOSE)) {
            prevPageFlags.removeFlag(InventoryFlag.CLEAR_HISTORY_ON_CLOSE);
            hadFlag = true;
        }
        this.page = page;
        var pageMenu = pages.get(page);
        handlePageButtons(pageMenu);
        pageMenu.open(player);
        if (hadFlag) {
            prevPageFlags.addFlag(InventoryFlag.CLEAR_HISTORY_ON_CLOSE);
        }
    }

    public RenderedButton getNextPageComponent() {
        return nextPageComponent;
    }

    public RenderedButton getPrevPageComponent() {
        return prevPageComponent;
    }

    public List<RenderedMenu> getPages() {
        return pages;
    }

    private void handlePageButtons(RenderedMenu menu) {
        handlePageButton(page+1,menu,nextPageComponent);
        handlePageButton(page-1,menu,prevPageComponent);
    }

    private void handlePageButton(int page, RenderedMenu menu, RenderedButton button) {

        var slots = button.getSlotSelection().slots();
        for (int slot : slots) {
            var components = menu.getComponents(slot);
            for (var component : components) {
                if (component instanceof PageButton) {
                    menu.getComponents().remove(component);
                }
            }
        }
        Consumer<CustomInventoryClickEvent> click;
        if (page >= pages.size() || page < 0) {
            if (!showDummyPageBtns) {
                return;
            }
            click = event -> {};
        } else {
            click = event -> open(page);
        }
        var pageBtn = new PageButton(button, click);
        menu.addComponent(pageBtn);
    }

    private static class PageButton extends RenderedButton {

        public PageButton(RenderedButton renderedButton, Consumer<CustomInventoryClickEvent> eventConsumer) {
            super(renderedButton.getItemStack().clone(),
                    renderedButton.getSlotSelection().clone(),
                    (event) -> {
                        renderedButton.getClickConsumer().accept(event);
                        eventConsumer.accept(event);
                    });
        }
    }
}
