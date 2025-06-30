package dao;

import entity.Item;
import entity.Menu;

import javax.swing.plaf.MenuBarUI;

public class MenuDao extends GenericDao<Menu> {
    public MenuDao() {super(Menu.class);}
}