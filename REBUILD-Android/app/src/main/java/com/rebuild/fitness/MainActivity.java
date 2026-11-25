package com.rebuild.fitness;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    final int BG=Color.rgb(13,17,19), PANEL=Color.rgb(25,32,34), GREEN=Color.rgb(183,255,75), MUTED=Color.rgb(155,168,164), WHITE=Color.WHITE;
    DB db; LinearLayout root, content, nav; String tab="Today"; String selectedDate=today();
    static String today(){ return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()); }
    static class Ex { String day,name,machine,detail; int sets,reps; Ex(String d,String n,String m,String de,int s,int r){day=d;name=n;machine=m;detail=de;sets=s;reps=r;} }
    final ArrayList<Ex> exercises=new ArrayList<>();
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG); db=new DB(this); seed(); show();}
    void seed(){
        add("A","Seated cable row","Seated cable row machine","Neutral grip; controlled range; light load",2,12);
        add("A","Lat pulldown","Lat pulldown machine","Pull smoothly; avoid leaning or straining",2,12);
        add("A","Leg extension","Leg extension machine","Controlled lower-body movement",2,15);
        add("A","Seated leg curl","Seated leg curl machine","Controlled tempo",2,15);
        add("A","Dumbbell biceps curl","Light dumbbells","Keep elbows comfortable; no swinging",2,12);
        add("A","Treadmill walk","Treadmill","Easy pace",0,10);
        add("B","Dumbbell lateral raise","Light dumbbells","Short comfortable range; no shrugging",2,12);
        add("B","Rear-delt machine","Reverse pec-deck / rear-delt machine","Very light; stop if chest or incision tension",2,12);
        add("B","Sit-to-stand","Bench / bodyweight","Smooth controlled repetitions",2,12);
        add("B","Calf raises","Bodyweight / calf machine","Use support if needed",2,15);
        add("B","Stationary bike","Upright stationary bike","Easy pace",0,15);
        add("C","Seated cable row","Seated cable row machine","Light load; controlled range",2,12);
        add("C","Biceps curl","Dumbbells","No swinging",2,12);
        add("C","Triceps pushdown","Cable station with rope/bar","Keep elbows close; light load",2,12);
        add("C","Leg extension","Leg extension machine","Controlled tempo",2,15);
        add("C","Seated leg curl","Seated leg curl machine","Controlled tempo",2,15);
        add("C","Treadmill walk","Treadmill","Easy pace",0,10);
    }
    void add(String d,String n,String m,String de,int s,int r){exercises.add(new Ex(d,n,m,de,s,r));}
    GradientDrawable bg(int color,int radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(radius);return g;}
    TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);t.setPadding(0,4,0,4);return t;}
    LinearLayout vertical(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    LinearLayout horizontal(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void show(){
        root=vertical();root.setBackgroundColor(BG);setContentView(root);
        LinearLayout header=vertical();header.setPadding(22,22,22,12);
        TextView brand=text("REBUILD  /  TRAINING LOG",13,GREEN,true);header.addView(brand);
        TextView title=text(tab,28,WHITE,true);header.addView(title);root.addView(header);
        ScrollView scroll=new ScrollView(this);content=vertical();content.setPadding(18,4,18,24);scroll.addView(content);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        nav=horizontal();nav.setPadding(8,10,8,10);nav.setBackgroundColor(PANEL);
        String[] tabs={"Today","Plan","Calendar","Progress"};
        for(String s:tabs){TextView b=text(s,12,s.equals(tab)?GREEN:MUTED,true);b.setGravity(Gravity.CENTER);b.setPadding(6,12,6,12);nav.addView(b,new LinearLayout.LayoutParams(0,-2,1));b.setOnClickListener(v->{tab=s;show();});}
        root.addView(nav);
        if(tab.equals("Today")) todayScreen(); else if(tab.equals("Plan")) planScreen(); else if(tab.equals("Calendar")) calendarScreen(); else progressScreen();
    }
    void card(LinearLayout parent, View child){LinearLayout wrap=vertical();wrap.setPadding(16,14,16,14);wrap.setBackground(bg(PANEL,22));wrap.addView(child);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=12;parent.addView(wrap,p);}
    void button(LinearLayout p,String label,Runnable r){TextView b=text(label,14,BG,true);b.setGravity(Gravity.CENTER);b.setPadding(14,13,14,13);b.setBackground(bg(GREEN,16));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=8;p.addView(b,lp);b.setOnClickListener(v->r.run());}
    void todayScreen(){
        card(content, text("YOUR NEXT SESSION",12,GREEN,true));
        String day=nextDay(); TextView name=text("Workout "+day+"  ·  "+dayName(day),23,WHITE,true);content.addView(name);
        content.addView(text("Keep it light. Build consistency, not intensity.",14,MUTED,false));
        button(content,"START WORKOUT",()->{tab="Plan";show();});
        card(content,text("RECOVERY RULE",16,WHITE,true));
        content.addView(text("Follow your surgeon’s specific restrictions. Upper-body clearance does not automatically mean unrestricted chest loading. Stop for incision tension, pain, swelling or unusual symptoms.",13,MUTED,false));
        int count=db.countSessions();card(content,text("TRAINING SESSIONS LOGGED",12,GREEN,true));content.addView(text(""+count,30,WHITE,true));
        content.addView(text("Your history is stored locally on this device.",12,MUTED,false));
    }
    String nextDay(){int n=db.countSessions()%3;return new String[]{"A","B","C"}[n];}
    String dayName(String d){return d.equals("A")?"Back + legs + biceps":d.equals("B")?"Shoulders + legs + cardio":"Upper body + legs";}
    void planScreen(){
        for(String d:new String[]{"A","B","C"}){
            TextView heading=text("DAY "+d+"  ·  "+dayName(d),17,GREEN,true);content.addView(heading);
            for(Ex e:exercises)if(e.day.equals(d)) exerciseCard(e);
        }
        card(content,text("NOT INCLUDED",13,GREEN,true));
        content.addView(text("Chest press, bench press, flyes, push-ups and dips are excluded until your surgeon explicitly clears chest loading.",13,MUTED,false));
    }
    void exerciseCard(Ex e){
        LinearLayout box=vertical();box.setPadding(14,12,14,12);box.setBackground(bg(PANEL,18));
        LinearLayout row=horizontal();
        TextView pic=text("▣",28,GREEN,true);pic.setGravity(Gravity.CENTER);pic.setBackground(bg(Color.rgb(39,51,43),14));LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(58,58);ip.rightMargin=12;row.addView(pic,ip);
        LinearLayout info=vertical();info.addView(text(e.name,16,WHITE,true));info.addView(text(e.machine,12,MUTED,false));row.addView(info,new LinearLayout.LayoutParams(0,-2,1));box.addView(row);
        box.addView(text(e.detail,12,MUTED,false));
        box.addView(text((e.sets==0?"Cardio · ":"Target · ")+e.sets+" sets × "+e.reps+" reps",12,GREEN,true));
        boolean done=db.isDone(selectedDate,e.name);
        CheckBox check=new CheckBox(this);check.setText("Mark exercise complete");check.setTextColor(WHITE);check.setButtonTintList(android.content.res.ColorStateList.valueOf(GREEN));check.setChecked(done);
        check.setOnCheckedChangeListener((v,c)->db.setDone(selectedDate,e.day,e.name,c));box.addView(check);
        button(box,"LOG WEIGHT / SETS / REPS",()->logExercise(e));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=10;content.addView(box,p);
    }
    void logExercise(Ex e){
        LinearLayout form=vertical();EditText weight=field("Weight (kg)");EditText sets=field("Sets completed");EditText reps=field("Reps per set");EditText note=field("Notes (optional)");
        form.addView(weight);form.addView(sets);form.addView(reps);form.addView(note);
        new AlertDialog.Builder(this).setTitle(e.name).setView(form).setPositiveButton("Save",(d,w)->{
            db.log(selectedDate,e.day,e.name,val(weight),val(sets),val(reps),note.getText().toString());db.setDone(selectedDate,e.day,e.name,true);show();
        }).setNegativeButton("Cancel",null).show();
    }
    EditText field(String hint){EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);e.setHintTextColor(MUTED);e.setTextColor(WHITE);return e;}
    double val(EditText e){try{return Double.parseDouble(e.getText().toString());}catch(Exception ex){return 0;}}
    void calendarScreen(){
        content.addView(text("TRAINING CALENDAR",13,GREEN,true));
        CalendarView cal=new CalendarView(this);cal.setDate(System.currentTimeMillis());content.addView(cal,new LinearLayout.LayoutParams(-1,300));
        cal.setOnDateChangeListener((v,y,m,d)->{selectedDate=String.format(Locale.US,"%04d-%02d-%02d",y,m+1,d);showDateLog();});
        showDateLog();
    }
    void showDateLog(){
        card(content,text("SESSION · "+selectedDate,15,WHITE,true));
        Cursor c=db.logs(selectedDate);if(c.getCount()==0)content.addView(text("No exercises logged for this date. Rest days count too.",13,MUTED,false));
        while(c.moveToNext()){
            content.addView(text(c.getString(0)+"  ·  "+c.getDouble(1)+" kg  ·  "+c.getInt(2)+" sets × "+c.getInt(3)+" reps",13,WHITE,false));
            if(!c.getString(4).isEmpty())content.addView(text(c.getString(4),12,MUTED,false));
        }c.close();
        button(content,"LOG AN EXERCISE FOR THIS DATE",()->{tab="Plan";show();});
    }
    void progressScreen(){
        card(content,text("YOUR TRAINING HISTORY",13,GREEN,true));
        content.addView(text("Sessions logged: "+db.countSessions(),26,WHITE,true));
        content.addView(text("Total logged sets: "+db.totalSets(),16,WHITE,true));
        content.addView(text("Logged training volume: "+String.format(Locale.US,"%.0f",db.volume())+" kg-reps",16,WHITE,true));
        content.addView(text("Recent sessions",14,GREEN,true));
        Cursor c=db.recent();while(c.moveToNext())content.addView(text(c.getString(0)+"  ·  Day "+c.getString(1)+"  ·  "+c.getInt(2)+" exercises",13,WHITE,false));c.close();
        button(content,"EXPORT BACKUP (JSON)",()->exportBackup());
    }
    void exportBackup(){
        String json=db.exportJson();Intent send=new Intent(Intent.ACTION_SEND);send.setType("application/json");send.putExtra(Intent.EXTRA_TEXT,json);startActivity(Intent.createChooser(send,"Export REBUILD backup"));
    }
    class DB extends SQLiteOpenHelper{
        DB(Context c){super(c,"rebuild.db",null,1);}
        public void onCreate(SQLiteDatabase d){
            d.execSQL("CREATE TABLE done(date TEXT, day TEXT, name TEXT, done INTEGER, PRIMARY KEY(date,name))");
            d.execSQL("CREATE TABLE logs(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT,day TEXT,name TEXT,weight REAL,sets INTEGER,reps INTEGER,note TEXT)");
        }
        public void onUpgrade(SQLiteDatabase d,int a,int b){}
        void setDone(String date,String day,String name,boolean done){SQLiteDatabase d=getWritableDatabase();ContentValues v=new ContentValues();v.put("date",date);v.put("day",day);v.put("name",name);v.put("done",done?1:0);d.insertWithOnConflict("done",null,v,SQLiteDatabase.CONFLICT_REPLACE);}
        boolean isDone(String date,String name){Cursor c=getReadableDatabase().rawQuery("SELECT done FROM done WHERE date=? AND name=?",new String[]{date,name});boolean b=c.moveToFirst()&&c.getInt(0)==1;c.close();return b;}
        void log(String date,String day,String name,double weight,double sets,double reps,String note){ContentValues v=new ContentValues();v.put("date",date);v.put("day",day);v.put("name",name);v.put("weight",weight);v.put("sets",(int)sets);v.put("reps",(int)reps);v.put("note",note);getWritableDatabase().insert("logs",null,v);}
        Cursor logs(String date){return getReadableDatabase().rawQuery("SELECT name,weight,sets,reps,note FROM logs WHERE date=? ORDER BY id",new String[]{date});}
        int countSessions(){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(DISTINCT date) FROM logs",null);c.moveToFirst();int n=c.getInt(0);c.close();return n;}
        int totalSets(){Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(sets),0) FROM logs",null);c.moveToFirst();int n=c.getInt(0);c.close();return n;}
        double volume(){Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(weight*sets*reps),0) FROM logs",null);c.moveToFirst();double n=c.getDouble(0);c.close();return n;}
        Cursor recent(){return getReadableDatabase().rawQuery("SELECT date,day,COUNT(*) FROM logs GROUP BY date,day ORDER BY date DESC LIMIT 12",null);}
        String exportJson(){StringBuilder s=new StringBuilder("{\"logs\":[");Cursor c=getReadableDatabase().rawQuery("SELECT date,day,name,weight,sets,reps,note FROM logs ORDER BY date",null);boolean first=true;while(c.moveToNext()){if(!first)s.append(",");first=false;s.append("{\"date\":\"").append(c.getString(0)).append("\",\"day\":\"").append(c.getString(1)).append("\",\"exercise\":\"").append(c.getString(2).replace("\"","'")).append("\",\"weightKg\":").append(c.getDouble(3)).append(",\"sets\":").append(c.getInt(4)).append(",\"reps\":").append(c.getInt(5)).append(",\"notes\":\"").append(c.getString(6).replace("\"","'")).append("\"}");}c.close();return s.append("]}").toString();}
    }
}