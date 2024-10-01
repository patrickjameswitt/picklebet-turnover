import java.util.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val name = "Kotlin"

    val trh = TurnoverRequirementHandler()
    val depositID = UUID.randomUUID()
    val userID = UUID.randomUUID()
    trh.deposit(userId = userID, depositId = depositID, amount = 100)
    trh.turnover(userId = userID, depositID, 50)
    trh.turnover(userID, depositID, 30)
    trh.turnover(userID, depositID, 20)

    val test = "fdfda"

}