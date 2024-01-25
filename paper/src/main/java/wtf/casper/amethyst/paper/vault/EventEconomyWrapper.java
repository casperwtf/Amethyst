package wtf.casper.amethyst.paper.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.events.vault.economy.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper around Economy implementation to fire events.
 *
 * @author Rsl1122, casperwtf
 */
public class EventEconomyWrapper implements Economy {

    private final Economy original;

    public EventEconomyWrapper(Economy original) {
        this.original = original;
    }

    // returns true if not cancelled
    private <T extends Event> Boolean callEvent(T event) {
        Bukkit.getPluginManager().callEvent(event);
        return !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
    }

    public boolean isEnabled() {
        return original.isEnabled();
    }

    public String getName() {
        return original.getName();
    }

    public boolean hasBankSupport() {
        return original.hasBankSupport();
    }

    public int fractionalDigits() {
        return original.fractionalDigits();
    }

    public String format(double amount) {
        return original.format(amount);
    }

    public String currencyNamePlural() {
        return original.currencyNamePlural();
    }

    public String currencyNameSingular() {
        return original.currencyNameSingular();
    }

    @Deprecated
    public boolean hasAccount(String playerName) {
        return original.hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return original.hasAccount(player);
    }

    @Deprecated
    public boolean hasAccount(String playerName, String worldName) {
        return original.hasAccount(playerName, worldName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return original.hasAccount(player, worldName);
    }

    @Deprecated
    public double getBalance(String playerName) {
        return original.getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return original.getBalance(player);
    }

    @Deprecated
    @Override
    public double getBalance(String playerName, String worldName) {
        return original.getBalance(playerName, worldName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String worldName) {
        return original.getBalance(player, worldName);
    }

    @Deprecated
    @Override
    public boolean has(String playerName, double amount) {
        return original.has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return original.has(player, amount);
    }

    @Deprecated
    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return original.has(Bukkit.getOfflinePlayer(playerName), worldName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String playerName, double amount) {
        return original.has(player, playerName, amount);
    }

    @Deprecated
    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (callEvent(new PrePlayerWithdrawEvent(Bukkit.getOfflinePlayer(playerName), amount))) {
            EconomyResponse response = original.withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount);
            callEvent(new PlayerWithdrawEvent(Bukkit.getOfflinePlayer(playerName), amount, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (callEvent(new PrePlayerWithdrawEvent(player, amount))) {
            EconomyResponse response = original.withdrawPlayer(player, amount);
            callEvent(new PlayerWithdrawEvent(player, amount, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Deprecated
    @Override
    public EconomyResponse withdrawPlayer(String playerName, String playerName1, double amount) {
        if (callEvent(new PrePlayerWithdrawEvent(Bukkit.getOfflinePlayer(playerName), amount, playerName1))) {
            EconomyResponse response = original.withdrawPlayer(Bukkit.getOfflinePlayer(playerName), playerName1, amount);
            callEvent(new PlayerWithdrawEvent(Bukkit.getOfflinePlayer(playerName), amount, playerName1, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String playerName, double amount) {
        if (callEvent(new PrePlayerWithdrawEvent(player, amount, playerName))) {
            EconomyResponse response = original.withdrawPlayer(player, playerName, amount);
            callEvent(new PlayerWithdrawEvent(player, amount, playerName, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Deprecated
    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (callEvent(new PrePlayerDepositEvent(Bukkit.getOfflinePlayer(playerName), amount))) {
            EconomyResponse response = original.depositPlayer(Bukkit.getOfflinePlayer(playerName), amount);
            callEvent(new PlayerDepositEvent(Bukkit.getOfflinePlayer(playerName), amount, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (callEvent(new PrePlayerDepositEvent(player, amount))) {
            EconomyResponse response = original.depositPlayer(player, amount);
            callEvent(new PlayerDepositEvent(player, amount, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Deprecated
    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        if (callEvent(new PrePlayerDepositEvent(Bukkit.getOfflinePlayer(playerName), amount, worldName))) {
            EconomyResponse response = original.depositPlayer(Bukkit.getOfflinePlayer(playerName), worldName, amount);
            callEvent(new PlayerDepositEvent(Bukkit.getOfflinePlayer(playerName), amount, worldName, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        if (callEvent(new PrePlayerDepositEvent(player, amount, worldName))) {
            EconomyResponse response = original.depositPlayer(player, worldName, amount);
            callEvent(new PlayerDepositEvent(player, amount, worldName, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Deprecated
    @Override
    public EconomyResponse createBank(String bankName, String playerName) {
        if (callEvent(new PreBankCreateEvent(bankName, Bukkit.getOfflinePlayer(playerName)))) {
            EconomyResponse response = original.createBank(bankName, Bukkit.getOfflinePlayer(playerName));
            callEvent(new BankCreateEvent(bankName, Bukkit.getOfflinePlayer(playerName), response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Override
    public EconomyResponse createBank(String bankName, OfflinePlayer player) {
        if (callEvent(new PreBankCreateEvent(bankName, player))) {
            EconomyResponse response = original.createBank(bankName, player);
            callEvent(new BankCreateEvent(bankName, player, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Override
    public EconomyResponse deleteBank(String accountName) {
        return original.deleteBank(accountName);
    }

    @Override
    public EconomyResponse bankBalance(String accountName) {
        return original.bankBalance(accountName);
    }

    @Override
    public EconomyResponse bankHas(String playerName, double amount) {
        return original.bankHas(playerName, amount);
    }

    @Override
    public EconomyResponse bankWithdraw(String accountName, double amount) {
        if (callEvent(new PreBankWithdrawEvent(accountName, amount))) {
            EconomyResponse response = original.bankWithdraw(accountName, amount);
            callEvent(new BankWithdrawEvent(accountName, amount, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Override
    public EconomyResponse bankDeposit(String playerName, double amount) {
        if (callEvent(new PreBankDepositEvent(playerName, amount))) {
            EconomyResponse response = original.bankDeposit(playerName, amount);
            callEvent(new BankDepositEvent(playerName, amount, response));
            return response;
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cancelled");
        }
    }

    @Deprecated
    @Override
    public EconomyResponse isBankOwner(String bankName, String playerName) {
        return original.isBankOwner(bankName, playerName);
    }

    @Override
    public EconomyResponse isBankOwner(String bankName, OfflinePlayer player) {
        return original.isBankOwner(bankName, player);
    }

    @Deprecated
    @Override
    public EconomyResponse isBankMember(String bankName, String playerName) {
        return original.isBankMember(bankName, playerName);
    }

    @Override
    public EconomyResponse isBankMember(String bankName, OfflinePlayer player) {
        return original.isBankMember(bankName, player);
    }

    @Override
    public List<String> getBanks() {
        return original.getBanks();
    }

    @Deprecated
    @Override
    public boolean createPlayerAccount(String playerName) {
        if (callEvent(new PrePlayerAccountCreateEvent(Bukkit.getOfflinePlayer(playerName)))) {
            boolean success = original.createPlayerAccount(playerName);
            callEvent(success ? new PlayerAccountCreateSuccessEvent(Bukkit.getOfflinePlayer(playerName)) : new PlayerAccountCreateFailedEvent(Bukkit.getOfflinePlayer(playerName)));
            return success;
        } else {
            return false;
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (callEvent(new PrePlayerAccountCreateEvent(player))) {
            boolean success = original.createPlayerAccount(player);
            callEvent(success ? new PlayerAccountCreateSuccessEvent(player) : new PlayerAccountCreateFailedEvent(player));
            return success;
        } else {
            return false;
        }
    }

    @Deprecated
    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        if (callEvent(new PrePlayerAccountCreateEvent(Bukkit.getOfflinePlayer(playerName), worldName))) {
            boolean success = original.createPlayerAccount(Bukkit.getOfflinePlayer(playerName), worldName);
            callEvent(success ? new PlayerAccountCreateSuccessEvent(Bukkit.getOfflinePlayer(playerName), worldName) : new PlayerAccountCreateFailedEvent(Bukkit.getOfflinePlayer(playerName), worldName));
            return success;
        } else {
            return false;
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String world) {
        if (callEvent(new PrePlayerAccountCreateEvent(player, world))) {
            boolean success = original.createPlayerAccount(player, world);
            callEvent(success ? new PlayerAccountCreateSuccessEvent(player, world) : new PlayerAccountCreateFailedEvent(player, world));
            return success;
        } else {
            return false;
        }
    }
}