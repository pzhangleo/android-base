import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import hope.base.utils.DateTimeUtils;

/**
 * Created by zhangpeng on 2017/8/25.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class DateTimeTests {

    Context mContext;

    @Test
    public void testDateTime() {
        mContext = InstrumentationRegistry.getContext();
        AndroidThreeTen.init(mContext);
        String dateStart = "01/14/2012 09:20:58";
        String dateStop = "01/14/2012 09:26:00";

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);

        Date d1 = null;
        Date d2 = null;
        long end = 1409586000000l;
        long cur = 1509586559512l;
        System.out.println(DateTimeUtils.getMinutes(end, cur) + "\n");
        System.out.println(DateTimeUtils.formatDate(end, "yyyy-MM-dd HH:mm:ss") + "\n");
        System.out.println(DateTimeUtils.formatDate(cur, "yyyy-MM-dd HH:mm:ss") + "\n");
        Assert.assertEquals(23419, DateTimeUtils.getMinutes(cur, end));
    }
}
