import androidx.compose.runtime.Composable
import com.sprheany.fundhelper.ui.FundNavGraph
import com.sprheany.fundhelper.ui.theme.FundTheme

@Composable
fun FundApp() {
    FundTheme {
        FundNavGraph()
    }
}
