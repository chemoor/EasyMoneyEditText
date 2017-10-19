package com.wajahatkarim3.easymoneywidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.joda.money.Money;
import org.joda.money.format.MoneyFormatException;

import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import static java.lang.Math.abs;
/**
 * The EditText widget for support of money requirements like currency, number formatting, comma formatting etc.
 * <p>
 * Add com.wajahatkarim3.easymoneywidgets.EasyMoneyEditText into your XML layouts and you are done!
 * For more information, check http://github.com/wajahatkarim3/EasyMoney-Widgets
 *
 * @author Wajahat Karim (http://wajahatkarim.com)
 * @version 1.0.0 01/20/2017
 */

 /**
  * Plaese, add the librery org.joda.Money
  * @Updated by Youssef EL CARTOUBI (che.moor@gmail.com)
  * @version 2.0.0 10/02/2017
  */
public class EasyMoneyEditText extends EditText {

    private String _currencySymbol;
    private boolean _showCurrency;
    private boolean _showCommas;
    private Money _money;


    public EasyMoneyEditText(Context context) {
        super(context);
        initView(context, null);
    }

    public EasyMoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        updateValue(getText().toString());
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        this.setSelection(this.getText().length());
    }

    private void initView(Context context, AttributeSet attrs) {
        // Setting Default Parameters
        _currencySymbol = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        _showCurrency = true;
        _showCommas = true;

        // Check for the attributes
        if (attrs != null) {
            // Attribute initialization
            final TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.EasyMoneyWidgets, 0, 0);
            try {
                String currnecy = attrArray.getString(R.styleable.EasyMoneyWidgets_currency_symbol);
                if (currnecy == null)
                    currnecy = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
                setCurrency(currnecy);

                _showCurrency = attrArray.getBoolean(R.styleable.EasyMoneyWidgets_show_currency, true);
                _showCommas = attrArray.getBoolean(R.styleable.EasyMoneyWidgets_show_commas, true);
            } finally {
                attrArray.recycle();
            }
        }

        // Add Text Watcher for Decimal formatting
        initTextWatchers();
    }

    private void initTextWatchers() {
        this.setLongClickable(false);
        this.setSelected(false);

        this.addTextChangedListener(new TextWatcher() {
            String beforeCharSequenceToString = "";
            String onCharSequenceToString = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeCharSequenceToString = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                EasyMoneyEditText.this.removeTextChangedListener(this);
                onCharSequenceToString = charSequence.toString();
                String onCharSequenceToStringWithPoint = charSequence.toString().replaceAll(",", ".");
                if (onCharSequenceToStringWithPoint.length() != 0 && beforeCharSequenceToString.length() != 0) {
                    try {
                        BigMoney bigMoney;
                        Money money;
                        bigMoney = BigMoney.parse(onCharSequenceToStringWithPoint);
                        if (abs(beforeCharSequenceToString.length() - onCharSequenceToString.length()) == 1) {//20170919 bug
                            if (beforeCharSequenceToString.length() < onCharSequenceToString.length()) {
                                bigMoney = bigMoney.multipliedBy(10);
                                money = bigMoney.toMoney();
                                onCharSequenceToStringWithPoint = money.toString();
                            } else {
                                if (beforeCharSequenceToString.length() > onCharSequenceToString.length()) {
                                    onCharSequenceToStringWithPoint = Money.parse(onCharSequenceToStringWithPoint).
                                            dividedBy(10, RoundingMode.DOWN).rounded(CurrencyUnit.of(Money.parse(onCharSequenceToStringWithPoint).
                                            getCurrencyUnit().getCurrencyCode()).getDecimalPlaces(), RoundingMode.UP).toString();
                                }
                            }
                            setText(onCharSequenceToStringWithPoint);
                        }
                        setText(onCharSequenceToStringWithPoint);
                    } catch (MoneyFormatException | NullPointerException | ArithmeticException | IllegalCurrencyException e) {
                        setText(beforeCharSequenceToString);
                        e.getMessage();
                    }
                } else {
                    if (onCharSequenceToStringWithPoint.length() == 0) {
                        setText(beforeCharSequenceToString);
                    }
                }
                setSelection(getText().length());

                EasyMoneyEditText.this.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void updateValue(String text) {
        setText(text);
    }


    private void setShowCurrency(boolean value) {
        _showCurrency = value;
        updateValue(getText().toString());
    }


    /**
     * Set the currency symbol for the edit text. (Default is US Dollar $).
     *
     * @param newSymbol the new symbol of currency in string
     */
    public void setCurrency(String newSymbol) {
        _currencySymbol = newSymbol;
        updateValue(getText().toString());
    }

    public void setMoney(Money newMoney) {
        _money = newMoney;
        setCurrency(_money.getCurrencyUnit().getCurrencyCode());
        updateValue(getText().toString());
    }

    /**
     * Set the currency symbol for the edit text. (Default is US Dollar $).
     *
     * @param locale the locale of new symbol. (Defaul is Locale.US)
     */
    public void setCurrency(Locale locale) {
        setCurrency(Currency.getInstance(locale).getCurrencyCode());
    }

    /**
     * Set the currency symbol for the edit text. (Default is US Dollar $).
     *
     * @param currency the currency object of new symbol. (Defaul is Locale.US)
     */
    public void setCurrency(Currency currency) {
        setCurrency(currency.getCurrencyCode());
    }

    /**
     * Whether currency is shown in the text or not. (Default is true)
     *
     * @return true if the currency is shown otherwise false.
     */
    public boolean isShowCurrency() {
        return _showCurrency;
    }

    /**
     * Shows the currency in the text. (Default is shown).
     */
    public void showCurrencySymbol() {
        setShowCurrency(true);
    }

    /**
     * Hides the currency in the text. (Default is shown).
     */
    public void hideCurrencySymbol() {
        setShowCurrency(false);
    }

    /**
     * Shows the commas in the text. (Default is shown).
     */
    public void showCommas() {
        _showCommas = true;
        updateValue(getText().toString());
    }

    /**
     * Hides the commas in the text. (Default is shown).
     */
    public void hideCommas() {
        _showCommas = false;
        updateValue(getText().toString());
    }

}
