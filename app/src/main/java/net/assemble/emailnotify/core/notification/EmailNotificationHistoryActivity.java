package net.assemble.emailnotify.core.notification;

import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import net.assemble.emailnotify.core.BuildConfig;
import net.assemble.emailnotify.core.EmailNotify;
import net.assemble.emailnotify.core.R;

/**
 * メール着信通知履歴表示 Activity
 */
public class EmailNotificationHistoryActivity extends ListActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_history);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * オプションメニューの生成
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * オプションメニューの選択
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    /**
     * ログ一覧表示を更新
     */
    private void updateList() {
        Cursor c = EmailNotificationHistoryDao.getHistories(this);
        if (c.moveToFirst()) {
            startManagingCursor(c);
            MyAdapter myAdapter = new MyAdapter(this, c);
            setListAdapter(myAdapter);
        } else {
            setListAdapter(null);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (BuildConfig.DEBUG) {
            Cursor cur = EmailNotificationHistoryDao.getHistory(this, id);
            if (cur.moveToFirst()) {
                String wapData = cur.getString(cur.getColumnIndex("wap_data"));
                String contentType = cur.getString(cur.getColumnIndex("content_type"));
                String applicationId = cur.getString(cur.getColumnIndex("application_id"));
                String mailbox = cur.getString(cur.getColumnIndex("mailbox"));
                Date timestampDate = null;
                long timestamp =  cur.getLong(cur.getColumnIndex("timestamp"));
                if (timestamp > 0) {
                    timestampDate = new Date(timestamp * 1000);
                }
                Date loggedDate = null;
                long logged =  cur.getLong(cur.getColumnIndex("logged_at"));
                if (logged > 0) {
                    loggedDate = new Date(logged * 1000);
                }
                Date notifiedDate = null;
                long notified =  cur.getLong(cur.getColumnIndex("notified_at"));
                if (notified > 0) {
                    notifiedDate = new Date(notified * 1000);
                }
                Date clearedDate = null;
                long cleared =  cur.getLong(cur.getColumnIndex("cleared_at"));
                if (cleared > 0) {
                    clearedDate = new Date(cleared * 1000);
                }

                StringBuilder textBuf = new StringBuilder();
                textBuf.append(wapData).append("\n\n");
                textBuf.append("Content-Type: ").append(contentType).append("\n");
                textBuf.append("X-Wap-Application-Id: ").append(applicationId).append("\n");
                textBuf.append("mailbox: ").append(mailbox).append("\n");

                if (timestampDate != null) {
                    textBuf.append("\nR ").append(timestampDate.toLocaleString());
                }
                if (loggedDate != null) {
                    textBuf.append("\nL ").append(loggedDate.toLocaleString());
                }
                if (notifiedDate != null) {
                    textBuf.append("\nN ").append(notifiedDate.toLocaleString());
                }
                if (clearedDate != null) {
                    textBuf.append("\nC ").append(clearedDate.toLocaleString());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Detail #" + id);
                builder.setMessage(textBuf.toString());
                builder.setPositiveButton(R.string.ok, null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    /**
     * DB→リスト表示
     */
    private class MyAdapter extends ResourceCursorAdapter {

        public MyAdapter(Context context, Cursor cur) {
            super(context, R.layout.email_history, cur);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.email_history_entry, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cur) {
            Date createdDate = new Date(cur.getLong(cur.getColumnIndex("created_at")) * 1000);
            String mailbox = cur.getString(cur.getColumnIndex("mailbox"));

            TextView createdText = (TextView)view.findViewById(R.id.created_at);
            createdText.setText(createdDate.toLocaleString());

            TextView mailboxText = (TextView)view.findViewById(R.id.mailbox);
            mailboxText.setText(mailbox);
            mailboxText.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
        }
    }

}
