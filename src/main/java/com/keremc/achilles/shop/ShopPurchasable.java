package com.keremc.achilles.shop;

import com.keremc.achilles.data.PlayerData;
import com.keremc.achilles.chat.Style;

public interface ShopPurchasable {

    int getPrice();

    default void purchased(PlayerData pd) {
        pd.getPlayer().sendMessage(Style.header("§b" + getPrice() + " §etokens has been §cdeducted §efrom your account."));
        pd.setBalance(pd.getBalance() - getPrice());
        pd.setCustomInv(true);
    }
}
