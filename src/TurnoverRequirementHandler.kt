import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

class TurnoverRequirementHandler {

    val map = ConcurrentHashMap<UUID, User>()

    /**
     * Registers a deposit for a given user.
     * @param userId ID of the user making the deposit.
     * @param depositId Unique ID of the deposit event.
     * @param amount The deposit amount.
     */
    fun deposit(userId: UUID, depositId: UUID, amount: Long) {
        map.getOrPut(userId) {
            User(userId, mutableListOf(), depositAmountToTurnOver = amount)
        }.addTransaction(depositId, amount, TransactionType.D)
    }

    /**
     * Records a turnover event for a given user.
     * @param userId ID of the user generating turnover.
     * @param eventId Unique ID of the turnover event.
     * @param amount The turnover amount.
     */
    fun turnover(userId: UUID, eventId: UUID, amount: Long) {
        val currentAmountToTurnOver = map[userId]?.depositAmountToTurnOver ?: 0
        val depositAmountToTurnOver = max(0, amount - currentAmountToTurnOver)
        map.getOrPut(userId) {
            User(userId, mutableListOf(), depositAmountToTurnOver = depositAmountToTurnOver)
        }.addTransaction(eventId, amount, TransactionType.T, transactionState = TransactionState.COMPLETE)
    }

    /**
     * Reverses a previously recorded turnover event.
     * @param userId ID of the user whose turnover is being reversed.
     * @param eventId Unique ID of the turnover event to reverse.
     */
    fun reverseTurnover(userId: UUID, eventId: UUID) {
        // Implementation goes here
        val user = map[userId]
        val turnoverToReverse = user?.transactions?.firstOrNull {
            eventId == it.depositId && it.transactionType == TransactionType.T
        }?.copy(transactionState = TransactionState.VOID)
        // i guess if its linked to a direct turn over we need to see that
    }

    /**
     * Reverses a previously registered deposit.
     * @param userId ID of the user whose deposit is being reversed.
     * @param depositId Unique ID of the deposit event to reverse.
     */
    fun reverseDeposit(userId: UUID, depositId: UUID) {
        // Implementation goes here
    }

    /**
     * Retrieves the total withdrawable amount for a user, considering their balance and turnover requirements.
     * @param userId ID of the user.
     * @param userBalance The user's current balance.
     * @return The amount available for withdrawal after satisfying turnover requirements.
     */
    fun getWithdrawableAmount(userId: UUID, userBalance: Long): Long {
        // Implementation goes here
        return 0L // Placeholder return
    }

    data class User(val userId: UUID,
                    val transactions: MutableList<Transaction>,
                    val depositAmountToTurnOver: Long = 0) {

        fun addTransaction( depositId: UUID, amount: Long, transactionType: TransactionType, transactionState: TransactionState) {
            val amountToAdd = max(0, amount)
            val transaction = Transaction(amountToAdd, depositId, transactionType, transactionState)
            transactions.add(transaction)
        }
    }


    data class Transaction(val amount: Long, val depositId: UUID, val transactionType: TransactionType, val transactionState: TransactionState)

    enum class TransactionType {
        T,
        D
    }

    enum class TransactionState {
        VOID,
        COMPLETE
        // could be pending etc.
    }
}