package kz.ftsystem.yel.fts;

import android.content.Intent;
import android.os.Bundle;

import com.ftsystem.yel.fts.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import kz.ftsystem.yel.fts.Interfaces.MyCallback;
import kz.ftsystem.yel.fts.backend.MyConstants;
import kz.ftsystem.yel.fts.backend.connection.Backend;
import kz.ftsystem.yel.fts.backend.database.DB;
import ru.cloudpayments.sdk.cp_card.CPCard;
import ru.cloudpayments.sdk.three_ds.ThreeDSDialogListener;
import ru.cloudpayments.sdk.three_ds.ThreeDsDialogFragment;

public class CheckoutActivity extends AppCompatActivity implements ThreeDSDialogListener, MyCallback {

    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = ' ';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;


    @BindView(R.id.edit_card_number)
    TextView etCardNumber;

    @BindView(R.id.edit_card_date)
    TextView etCardDate;

    @BindView(R.id.edit_card_holder_name)
    TextView etCardHolderName;

    @BindView(R.id.edit_card_cvc)
    TextView etCardCVC;

    @BindView(R.id.progressBarCheckout)
    ProgressBar progressBar;

    String myId;
    String myToken;
    String myEmail;
    String orderId;
    String amount;
    String ipAddr;
    String cardCryptogram = null;
    String cardHolderName;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        DB preferences = new DB(this);
        preferences.open();
        myId = preferences.getVariable(MyConstants.MY_ID);
        myToken = preferences.getVariable(MyConstants.MY_TOKEN);
        orderId = preferences.getVariable(MyConstants.ORDER_ID);
        ipAddr = preferences.getVariable(MyConstants.MY_IP_ADDR);
        myEmail = preferences.getVariable(MyConstants.MY_EMAIL);
        token = preferences.getVariable(MyConstants.MY_TOKEN);
        preferences.close();


        if (getIntent().hasExtra("amount")) {
            amount = getIntent().getStringExtra("amount");
        } else {
            amount = "error";
        }
    }

    @OnTextChanged(value = R.id.edit_card_number, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.edit_card_date, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardDateTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.edit_card_cvc, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardCVCTextChanged(Editable s) {
        if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
            s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
        }
    }

    @OnTextChanged(value = R.id.edit_card_holder_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardHolderName(Editable s) {

    }

    @OnClick(R.id.button_payment)
    void onPaymentClick() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        String cardNumber = etCardNumber.getText().toString().replace(" ", "");
        String cardDate = etCardDate.getText().toString().replace("/", "");
        String cardCVC = etCardCVC.getText().toString();
        cardHolderName = etCardHolderName.getText().toString();

        // Проверям номер карты.
        if (!CPCard.isValidNumber(cardNumber)) {
            etCardNumber.setError(getResources().getString(R.string.checkout_card_number));
//            Toast.makeText(this, R.string.checkout_error_card_number, Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверям срок действия карты.
        if (!CPCard.isValidExpDate(cardDate)) {
            etCardDate.setError(getResources().getString(R.string.checkout_error_card_date));
//            Toast.makeText(this, R.string.checkout_error_card_date, Toast.LENGTH_LONG).show();
            return;
        }

        // Проверям cvc код карты.
        if (cardCVC.length() != 3) {
            etCardCVC.setError(getResources().getString(R.string.checkout_error_card_cvc));
//            Toast.makeText(this, R.string.checkout_error_card_cvc, Toast.LENGTH_LONG).show();
            return;
        }

        // После проверики, если все данные корректны, создаем объект CPCard, иначе при попытке создания объекта CPCard мы получим исключение.
        CPCard card = new CPCard(cardNumber, cardDate, cardCVC);

        // Тип карты: VISA или MasterCard и т.д.
        String cardType = card.getType();

        // Создаем криптограмму карточных данных
        try {
            // Чтобы создать криптограмму необходим PublicID (его можно посмотреть в личном кабинете)
            cardCryptogram = card.cardCryptogram(MyConstants.MERCHANT_PUBLIC_ID);
        } catch (UnsupportedEncodingException | StringIndexOutOfBoundsException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        if (cardCryptogram != null) {
            Backend backend = new Backend(this, this);
            backend.paying(amount, "KZT", ipAddr, cardHolderName, cardCryptogram, myId,
                    token, orderId, myEmail);
        }
    }

    @Override
    public void onAuthorizationCompleted(String md, String paRes) {
        Backend backend = new Backend(this, this);
        backend.makeAnOrder(myId, myToken, orderId);
        Log.d(MyConstants.TAG, "COMPLETED");
    }

    @Override
    public void onAuthorizationFailed(String html) {
        Log.d(MyConstants.TAG, "FAILED");
    }

    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    @Override
    public void fromBackend(HashMap<String, String> data) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        switch (data.get("response")) {
            case "3ds":
                ThreeDsDialogFragment.newInstance(data.get("acsUrl"),
                        data.get("md"),
                        data.get("paReq"))
                        .show(getFragmentManager(), "3DS");
                break;
            case "send_error":
                Toast.makeText(this, R.string.error_send_data, Toast.LENGTH_SHORT).show();
                break;
            case "denied":
                Toast.makeText(this, R.string.ora_access_denied, Toast.LENGTH_SHORT).show();
                break;
            case "field_error":
            case "error_f":
                Toast.makeText(this, R.string.error_send, Toast.LENGTH_SHORT).show();
                break;
            case "completed":
                Backend backend = new Backend(this, this);
                backend.makeAnOrder(myId, token, orderId);
                break;
            case "declined":
                Toast.makeText(this, R.string.payment_declined, Toast.LENGTH_SHORT).show();
                break;
            case "pay_error":
                Toast.makeText(this, "Ошибка: " + data.get("message"), Toast.LENGTH_LONG).show();
                break;
            case "unknown_error":
                Toast.makeText(this, R.string.error_unknown, Toast.LENGTH_LONG).show();
                break;
//          Callback от makeAnOrder
            case "error_no_order":
                Toast.makeText(this, R.string.ora_error_order_id, Toast.LENGTH_SHORT).show();
                break;
            case "ok":
                DB preferences = new DB(this);
                preferences.open();
                preferences.setVariable(MyConstants.IS_ORDERED, "0");
                preferences.setVariable(MyConstants.MAKE_ORDER, "1");
                preferences.close();
                Intent intent = new Intent(this, WaitingTranslatorActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}


