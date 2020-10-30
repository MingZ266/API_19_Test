package com.tai.api19test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class FileActivity extends AppCompatActivity {
    String TAG = "FileActivityTAG";
    private Context context = FileActivity.this;
    private String path, rootPath;
    private File[] files = null;
    private Gson gson = new Gson();

    private TextView filePath;
    private ListView fileLook;
    private Button createFile, createPaperFile, deleteAll;
    private Button createSP;
    private Button objectOut, objectIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        initView();
        myProcess();
        myListener();
    }

    private void initView() {
        filePath = findViewById(R.id.filePath);
        fileLook = findViewById(R.id.fileLook);
        createFile = findViewById(R.id.createFile);
        createPaperFile = findViewById(R.id.createPaperFile);
        deleteAll = findViewById(R.id.deleteAll);
        createSP = findViewById(R.id.createSP);
        objectOut = findViewById(R.id.objectOut);
        objectIn = findViewById(R.id.objectIn);
    }

    private void myProcess() {
        path = getCacheDir().getAbsolutePath();
        Log.d(TAG, "测试：" + path);
        rootPath = path.substring(0, path.lastIndexOf('/'));
        filePath.setText(path);
        refresh();
    }

    private void myListener() {
        fileLook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (!path.equals(rootPath)) {
                        path = path.substring(0, path.lastIndexOf('/'));
                        filePath.setText(path);
                        refresh();
                    }
                } else {
                    if (files[position - 1].isDirectory()) {
                        path = files[position - 1].getAbsolutePath();
                        filePath.setText(path);
                        refresh();
                    } else
                        showWriteDialog(files[position - 1]);
                }
            }
        });

        fileLook.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position != 0) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                            .setTitle(files[position - 1].getName())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (deleteFile(files[position - 1])) {
                                        Toast.makeText(context, "已删除", Toast.LENGTH_SHORT).show();
                                        refresh();
                                    } else
                                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("取消", null);
                    if (files[position - 1].isDirectory())
                        dialogBuilder.setMessage("将删除该目录及其以下文件？");
                    else
                        dialogBuilder.setMessage("删除该文件？");
                    dialogBuilder.create().show();
                }
                return true;
            }
        });

        createFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateDialog(true);
            }
        });

        createPaperFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateDialog(false);
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                        .setTitle(path.substring(path.lastIndexOf('/')))
                        .setMessage("将删除当前目录下所有文件(夹)？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (files.length == 0)
                                    Toast.makeText(context, "目录为空", Toast.LENGTH_SHORT).show();
                                else {
                                    for (File file : files) {
                                        if (!deleteFile(file))
                                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(context, "已删除", Toast.LENGTH_SHORT).show();
                                    refresh();
                                }
                            }
                        })
                        .setNegativeButton("取消", null);
                dialogBuilder.create().show();
            }
        });

        createSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "Mr_Tai";
                SharedPreferences sp = getSharedPreferences(fileName, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("name", "邰启超");
                editor.putInt("age", 18);
                editor.apply();
                Log.d(TAG, String.valueOf(sp.getString("name", "A")));
                Log.d(TAG, String.valueOf(sp.getInt("age", 0)));
            }
        });

        objectOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog view = showClassDialog();
                Button OK = view.findViewById(R.id.OK);
                if (OK != null)
                    OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Person person = new Person();
                            EditText nameEdit = view.findViewById(R.id.name);
                            EditText sexEdit = view.findViewById(R.id.sex);
                            EditText ageEdit = view.findViewById(R.id.age);
                            EditText birthEdit = view.findViewById(R.id.birthday);
                            EditText workEdit = view.findViewById(R.id.work);
                            String name, sex, birthday, work;
                            int age;
                            if (nameEdit == null)
                                name = "";
                            else
                                name = String.valueOf(nameEdit.getText());
                            if (sexEdit == null)
                                sex = "";
                            else
                                sex = String.valueOf(sexEdit.getText());
                            try {
                                if (ageEdit == null)
                                    age = 0;
                                else
                                    age = Integer.parseInt(String.valueOf(ageEdit.getText()));
                            }catch (Exception e) {
                                age = 0;
                            }
                            if (birthEdit == null)
                                birthday = "";
                            else
                                birthday = String.valueOf(birthEdit.getText());
                            if (workEdit == null)
                                work = "";
                            else
                                work = String.valueOf(workEdit.getText());
                            if (!name.equals(""))
                                person.setName(name);
                            switch (sex) {
                                case "男":
                                    person.setSex(Person.SexEnum.MAN);
                                    break;
                                case "女":
                                    person.setSex(Person.SexEnum.WOMAN);
                                    break;
                            }
                            if (age != 0)
                                person.setAge(age);
                            String[] date = birthday.split("-");
                            if (date.length == 3) {
                                try {
                                    person.setBirthday(new Person.Birthday(
                                            Integer.parseInt(date[0]),
                                            Integer.parseInt(date[1]),
                                            Integer.parseInt(date[2])
                                    ));
                                } catch (Exception e) {
                                    Log.d(TAG, "出生日格式错误");
                                }
                            }
                            if (!work.equals(""))
                                person.setWork(work);
                            Log.d(TAG, person.displayMsg());

                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(new File(path, person.name));
                                // 将对象person写入文件
                                String personStr = gson.toJson(person);
                                Log.d(TAG, "personStr：" + personStr);
                                fos.write(personStr.getBytes());
                            } catch (IOException e) {
                                Log.d(TAG, "打开文件输出流失败");
                            } finally {
                                try {
                                    if (fos != null)
                                        fos.close();
                                } catch (IOException e) {
                                    Log.d(TAG, "关闭文件输出流失败");
                                }
                            }
                            refresh();
                            view.cancel();
                        }
                    });
                else
                    view.cancel();
            }
        });

        objectIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = View.inflate(context, R.layout.dialog_file_object_in, null);
                final AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(view)
                        .create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
                view.findViewById(R.id.OK).setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        String name = String.valueOf(((EditText) view.findViewById(R.id.getName)).getText());
                        if (!name.equals("")) {
                            dialog.cancel();
                            AlertDialog dialog1 = showClassDialog();
                            EditText nameEdit = dialog1.findViewById(R.id.name);
                            EditText sexEdit = dialog1.findViewById(R.id.sex);
                            EditText ageEdit = dialog1.findViewById(R.id.age);
                            EditText birthEdit = dialog1.findViewById(R.id.birthday);
                            EditText workEdit = dialog1.findViewById(R.id.work);

                            Person person = null;
                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(new File(path, name));
                                // 从文件读取对象
                                int len;
                                byte[] in = new byte[1024];
                                StringBuffer inStr = new StringBuffer();
                                while ((len = fis.read(in)) > 0) {
                                    inStr.append(new String(in, 0, len));
                                }
                                Log.d(TAG, "读得对象字符串：" + inStr);
                                person = gson.fromJson(String.valueOf(inStr), Person.class);
                                if (person != null)
                                    Log.d(TAG, "读取得：" + person.displayMsg());
                            } catch (FileNotFoundException e) {
                                Log.d(TAG, "找不到文件：" + name);
                            } catch (IOException e) {
                                Log.d(TAG, "读取文件内容失败");
                            } finally {
                                try {
                                    if (fis != null)
                                        fis.close();
                                } catch (IOException e) {
                                    Log.d(TAG, "关闭文件输入流失败");
                                }
                            }

                            if (person != null) {
                                if (nameEdit != null) {
                                    nameEdit.setText(person.name);
                                    nameEdit.setEnabled(false);
                                }
                                if (sexEdit != null) {
                                    switch (person.sex) {
                                        case MAN:
                                            sexEdit.setText("男");
                                            break;
                                        case WOMAN:
                                            sexEdit.setText("女");
                                            break;
                                    }
                                    sexEdit.setEnabled(false);
                                }
                                if (ageEdit != null) {
                                    ageEdit.setText(String.valueOf(person.age));
                                    ageEdit.setEnabled(false);
                                }
                                if (birthEdit != null) {
                                    birthEdit.setText(
                                            person.birthday.year + "-" +
                                            person.birthday.month + "-" +
                                            person.birthday.day
                                    );
                                    birthEdit.setEnabled(false);
                                }
                                if (workEdit != null) {
                                    workEdit.setText(person.work);
                                    workEdit.setEnabled(false);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private boolean deleteFile(File file) {
        if (file == null)
            return false;
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null)
                    return false;
                for (File f : files)
                    if (!deleteFile(f))
                        return false;
            }
            return file.delete();
        }
        return false;
    }

    /**
     * 展示建立文件(夹)的提示弹窗.
     *
     * @param isFile true:创建文件  false:创建文件夹
     */
    private void showCreateDialog(final boolean isFile) {
        final View dialogCreate = View.inflate(context, R.layout.dialog_file_create, null);
        final AlertDialog createDialog = new AlertDialog.Builder(context, R.style.CircleCornerDialog)
                .setView(dialogCreate)
                .create();
        createDialog.show();
        createDialog.setCanceledOnTouchOutside(false);

        final EditText inputName = dialogCreate.findViewById(R.id.inputName);
        if (isFile) {
            ((TextView) dialogCreate.findViewById(R.id.title)).setText("创建文件");
            inputName.setHint("请输入文件名");
        } else {
            ((TextView) dialogCreate.findViewById(R.id.title)).setText("创建目录");
            inputName.setHint("请输入目录名,多级目录以\"/\"划分");
        }

        dialogCreate.findViewById(R.id.getName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = String.valueOf(inputName.getText());
                if (!"".equals(name)) {
                    File file = new File(path, name);
                    if (isFile) {
                        try {
                            if (file.createNewFile()) {
                                Toast.makeText(context, "创建成功", Toast.LENGTH_SHORT).show();
                                createDialog.cancel();
                                refresh();
                                showWriteDialog(file);
                            }
                            else
                                Toast.makeText(context, "创建失败", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(context, "未知错误，创建失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (file.mkdirs()) {
                            Toast.makeText(context, "创建成功", Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                        else
                            Toast.makeText(context, "创建失败", Toast.LENGTH_SHORT).show();
                    }
                    createDialog.cancel();
                }
            }
        });

        dialogCreate.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog.cancel();
            }
        });
    }

    /**
     * 展示编辑文件内容的弹窗.
     *
     * @param file 欲编辑的文件
     */
    private void showWriteDialog(final File file) {
        if (file == null) {
            Toast.makeText(context, "欲编辑文件异常", Toast.LENGTH_SHORT).show();
            return;
        }
        final View dialogWrite = View.inflate(context, R.layout.dialog_file_write, null);
        final AlertDialog writeDialog = new AlertDialog.Builder(context, R.style.CircleCornerDialog)
                .setView(dialogWrite)
                .create();
        writeDialog.show();
        writeDialog.setCanceledOnTouchOutside(false);

        final EditText fileText = dialogWrite.findViewById(R.id.fileText);
        // 展示文件路径
        ((TextView) dialogWrite.findViewById(R.id.filePath)).setText(file.getAbsolutePath());
        // 读取文件内容并展示
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int len;
            byte[] in = new byte[1024];
            StringBuffer inStr = new StringBuffer();
            while ((len = fis.read(in)) > 0) {
                inStr.append(new String(in, 0, len));
            }
            fileText.setText(inStr);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "获取文件失败", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "读取文件内容失败", Toast.LENGTH_SHORT).show();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e(TAG, "文件 " + file.getAbsolutePath() + " 的输入流关闭失败");
                }
            }
        }
        // 保存文件内容
        dialogWrite.findViewById(R.id.OK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fos.write(String.valueOf(fileText.getText()).getBytes());
                    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    Toast.makeText(context, "获取文件失败", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(context, "保存文件内容失败", Toast.LENGTH_SHORT).show();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            Log.e(TAG, "文件 " + file.getAbsolutePath() + " 的输出流关闭失败");
                        }
                    }
                }
                writeDialog.cancel();
            }
        });
        // 取消编辑文件内容
        dialogWrite.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeDialog.cancel();
            }
        });
    }

    /**
     * 展示空白的Person类的弹窗.
     *
     * @return 弹窗的引用
     */
    private AlertDialog showClassDialog() {
        View view = View.inflate(context, R.layout.dialog_file_object, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        view.findViewById(R.id.OK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        return dialog;
    }

    private void refresh() {
        files = new File(path).listFiles();
        if (files == null) {
            Toast.makeText(context, "目录错误或目录不可访问", Toast.LENGTH_SHORT).show();
            files = new File[0];
        } else if (files.length != 0){
            int directoryCount = 0, filesLen = files.length;
            File[] tempDirectories = new File[filesLen];
            for (int i = 0; i < filesLen; i++)
                if (files[i].isDirectory()) {
                    tempDirectories[directoryCount++] = files[i];
                    System.arraycopy(files, i + 1, files, i, filesLen - i - 1);
                    i--;
                    filesLen--;
                }
            if (directoryCount > 0) {
                File[] directories = new File[directoryCount];
                System.arraycopy(tempDirectories, 0, directories, 0, directoryCount);
                Arrays.sort(directories);
                File[] newFiles = new File[filesLen];
                System.arraycopy(files, 0, newFiles, 0, filesLen);
                Arrays.sort(newFiles);
                System.arraycopy(directories, 0, files, 0, directories.length);
                System.arraycopy(newFiles, 0, files, directories.length, newFiles.length);
            } else
                Arrays.sort(files);
        }
        fileLook.setAdapter(new FileLookAdapter());
    }

    private class FileLookAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (files == null)
                return 0;
            return files.length + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(context, R.layout.list_item_file_file_look, null);
                viewHolder.fileType = convertView.findViewById(R.id.fileType);
                viewHolder.fileName = convertView.findViewById(R.id.fileName);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();
            if (files != null) {
                if (position == 0) {
                    viewHolder.fileType.setImageDrawable(getResources().getDrawable(R.drawable.ic_file_return));
                    viewHolder.fileName.setText("..");
                } else {
                    if (files[position - 1].isFile())
                        viewHolder.fileType.setImageDrawable(getResources().getDrawable(R.drawable.ic_file));
                    else if (files[position - 1].isDirectory())
                        viewHolder.fileType.setImageDrawable(getResources().getDrawable(R.drawable.ic_paper_file));
                    else
                        viewHolder.fileType.setImageDrawable(getResources().getDrawable(R.drawable.ic_file_unknow));
                    viewHolder.fileName.setText(files[position - 1].getName());
                }
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView fileType;
            TextView fileName;
        }
    }

    public static class Person implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private SexEnum sex;
        private int age;
        private Birthday birthday;
        private String work;

        Person() {
            name = "null";
            sex = SexEnum.MAN;
            age = 0;
            birthday = new Birthday(2020, 1, 1);
            work = "null";
        }

        void setName(String name) {
            this.name = name;
        }

        void setSex(SexEnum sex) {
            this.sex = sex;
        }

        void setAge(int age) {
            this.age = age;
        }

        void setBirthday(Birthday birthday) {
            this.birthday = birthday;
        }

        void setWork(String work) {
            this.work = work;
        }

        String displayMsg() {
            return "Person {" +
                    "\n\t姓名：" + name +
                    "\n\t性别：" + sex +
                    "\n\t年龄：" + age +
                    "\n\t生日：" + birthday.displayMsg() +
                    "\n\t职业：" + work +
                    "\n}";
        }

        private enum SexEnum {MAN, WOMAN}

        private static class Birthday {
            int year;
            int month;
            int day;

            Birthday(int year, int month, int day) {
                this.year = year;
                this.month = month;
                this.day = day;
            }

            String displayMsg() {
                return year + "年" + month + "月" + day + "日";
            }
        }
    }
}