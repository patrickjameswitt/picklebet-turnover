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

    //personally I would use say depositId should be transactionId
    fun deposit(userId: UUID, depositId: UUID, amount: Long) {
        val user = map.getOrPut(userId) {
            User(userId, mutableListOf(), depositAmountToTurnOver = amount)
        }
        user.addTransaction(depositId, amount, TransactionType.D, TransactionState.PENDING_TURNOVER, depositId, amount)
    }

    /**
     * Records a turnover event for a given user.
     * @param userId ID of the user generating turnover.
     * @param eventId Unique ID of the turnover event.
     * @param amount The turnover amount.
     */
    fun turnover(userId: UUID, eventId: UUID, amount: Long) {
        val user = map[userId]
        val confirmOriginalDeposit = user?.transactions?.firstOrNull {
            it.eventId == eventId && it.transactionType == TransactionType.D
        }
        val turnoverAmountRemaining = max(0, confirmOriginalDeposit!!.turnoverAmountRemaining - amount)

        if (confirmOriginalDeposit.transactionType == TransactionType.D) {
            val newTransactionState = if (turnoverAmountRemaining > 0) TransactionState.PENDING_TURNOVER else TransactionState.COMPLETED
            val depositTransaction = user.transactions.find { it.transactionType == TransactionType.D && it.eventId == eventId }!!
            depositTransaction.turnoverAmountRemaining = turnoverAmountRemaining
            depositTransaction.transactionState = newTransactionState
        }
        val turnover = Transaction(UUID.randomUUID(), eventId , amount, TransactionType.T, TransactionState.COMPLETED,
            eventId, 0 )
        user.transactions.add(turnover)
    }

    /**
     * Reverses a previously recorded turnover event.
     * @param userId ID of the user whose turnover is being reversed.
     * @param eventId Unique ID of the turnover event to reverse.
     */
    fun reverseTurnover(userId: UUID, eventId: UUID) = {
        val turnoverToReverse = map[userId]?.transactions.fi
    }

    /**
     * Reverses a previously registered deposit.
     * @param userId ID of the user whose deposit is being reversed.
     * @param depositId Unique ID of the deposit event to reverse.
     */
    fun reverseDeposit(userId: UUID, transactionId: UUID) {

    }

    /**
     * Retrieves the total withdrawable amount for a user, considering their balance and turnover requirements.
     * @param userId ID of the user.
     * @param userBalance The user's current balance.
     * @return The amount available for withdrawal after satisfying turnover requirements.
     */
    fun getWithdrawableAmount(userId: UUID, userBalance: Long): Long {
        // Implementation goes here
        val user = map[userId] ?: throw IllegalArgumentException("Unable to find user")

        return userBalance - user.depositAmountToTurnOver // Placeholder return
    }

    data class User(val userId: UUID,
                    val transactions: MutableList<Transaction>,
                    var depositAmountToTurnOver: Long = 0) {

        fun addTransaction( depositId: UUID, amount: Long, transactionType: TransactionType, transactionState: TransactionState, eventId: UUID, turnoverAmountRemaining: Long) {
            val amountToAdd = max(0, amount)
            val transactionId = UUID.randomUUID()
            val transaction = Transaction(transactionId, depositId, amountToAdd, transactionType, transactionState, eventId, turnoverAmountRemaining)
            transactions.add(transaction)
        }
    }

    //probably should have transaction interface that DepositTransaction & TurnoverTransaction implement... but we will keep it simple.
    data class Transaction(val transactionId: UUID = UUID.randomUUID(), val depositId: UUID, var amount: Long,
                           val transactionType: TransactionType, var transactionState: TransactionState, val eventId: UUID,
                           var turnoverAmountRemaining: Long
        )

    enum class TransactionType {
        T,
        D
    }

    enum class TransactionState {
        REVERSED,
        PENDING_TURNOVER,
        COMPLETED
        // could be pending etc.
    }

}