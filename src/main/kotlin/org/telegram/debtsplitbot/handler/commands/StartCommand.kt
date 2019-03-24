package org.telegram.debtsplitbot.handler.commands


class StartCommand : Command() {

    override fun execute(command: String): Boolean {
        handler.sendMessage(
            "Hi! This bot will help you to split debts between your friends :) Here is the list of its commands:\n\n" +
                    "/start - read info about this bot\n\n" +
                    "/newlist - create a new list of debts:\n/newlist [currency] [names] | /newlist USD John,Peter,Ann\n\n" +
                    "/debt - add a debt (without 'debtors' applies to each debtor):\n/debt [lender] [debtors*] [amount] | /debt John 21.33 | /debt John Peter,Ann 10.52\n\n" +
                    "/addparticipant - add a participant to the list:\n/addparticipant [participant] | /addparticipant Kate \n\n" +
                    "/setcurrency - switch between currencies or create one if does not exist:\n/setcurrency [currency] | /setcurrency EUR\n\n" +
                    "/result - see results:\n/result  [target currency*] [rates*] | /result | /result USD | /result USD EUR:0.86,GBP:0.75"
        )
        return false
    }


}