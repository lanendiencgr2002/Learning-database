# 导入操作系统相关的模块
import os
import time
# 导入图形界面库tkinter
import tkinter as tk
# 从tkinter导入一些常用的组件和对话框
from tkinter import ttk, filedialog, messagebox
# 导入Windows COM接口操作库
import win32com.client
# 导入剪贴板操作库
import pyperclip

# 获取桌面快捷方式所指向的可执行文件
def 计时(f):
    def wrapper(*args, **kwargs):
        start = time.time()
        result = f(*args, **kwargs)
        print(f'{f.__name__} 耗时：{time.time()-start:.4f}秒')
        return result
    return wrapper
@计时
def search_exe_in_folder(folder_path):
    exe_list = []
    for root, dirs, files in os.walk(folder_path):
        for file in files:
            if file.lower().endswith('.exe'):
                full_path = os.path.join(root, file)
                exe_list.append((os.path.splitext(file)[0], full_path))
    return exe_list
@计时
def get_desktop_shortcuts():
    desktop_path = os.path.join(os.path.join(os.environ['USERPROFILE']), 'Desktop')
    software_list = []
    shell = win32com.client.Dispatch("WScript.Shell")

    def traverse_directory(path):
        for item in os.listdir(path):
            item_path = os.path.join(path, item)
            if os.path.isdir(item_path):
                # 如果是文件夹，递归遍历
                traverse_directory(item_path)
            elif item.endswith(".lnk"):
                shortcut = shell.CreateShortcut(item_path)  # 创建一个快捷方式对象，用于读取快捷方式文件的属性
                target_path = shortcut.Targetpath # 读取这个快捷方式实际指向的文件路径
                if target_path.endswith(".exe"): # splitext：将文件名分割成文件名和扩展名
                    software_list.append((os.path.splitext(item)[0], target_path))
            # elif item.lower().endswith(".exe"):
            #     software_name = os.path.splitext(item)[0]
            #     software_list.append((software_name, item_path))

    # 开始遍历桌面
    traverse_directory(desktop_path)
    return software_list
@计时
# 获取已安装的软件列表
def get_installed_software():
    # 初始化存储软件信息的列表
    software_list = []
    try:
        # 尝试导入Windows注册表操作库
        import winreg
        # 设置注册表路径
        reg_path = r"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall"
        # 打开注册表键
        reg_key = winreg.OpenKey(winreg.HKEY_LOCAL_MACHINE, reg_path)
        # 遍历注册表中的所有子键
        for i in range(winreg.QueryInfoKey(reg_key)[0]):
            # 获取子键名
            sub_key_name = winreg.EnumKey(reg_key, i)
            # 打开子键
            sub_key = winreg.OpenKey(reg_key, sub_key_name)
            try:
                # 尝试获取软件显示名称和安装位置
                display_name = winreg.QueryValueEx(sub_key, "DisplayName")[0]
                install_location = winreg.QueryValueEx(sub_key, "InstallLocation")[0]
                # 如果两者都存在，则添加到软件列表
                if display_name and install_location:
                    software_list.append((display_name, install_location))
            except FileNotFoundError:
                # 如果找不到相关信息，继续下一个
                continue
    except ImportError:
        # 如果无法导入winreg，使用示例数据
        software_list = [("Example Software 1", "C:\\Program Files\\Example Software 1"),
                         ("Example Software 2", "C:\\Program Files\\Example Software 2")]
    # 返回软件列表
    return software_list
# 搜索可执行文件
def find_executable(display_name, install_location):
    # 初始化存储可执行文件的列表
    exe_files = []
    # 遍历安装目录及其子目录
    for root, dirs, files in os.walk(install_location):
        for file in files:
            # 如果文件是可执行文件（.exe），添加到列表
            if file.endswith(".exe"):
                exe_files.append(os.path.join(root, file))

    # 优先考虑与软件名相近的可执行文件
    for exe in exe_files:
        if display_name.lower() in os.path.basename(exe).lower():
            return exe

    # 如果未找到相近的可执行文件，返回所有可执行文件，让用户选择
    return exe_files
# 生成批处理文件
def generate_batch_file(selected_software):
    batch_file_path = filedialog.asksaveasfilename(defaultextension=".bat", filetypes=[("Batch files", "*.bat")])
    if not batch_file_path:
        return

    with open(batch_file_path, 'w') as batch_file:
        # 添加自动提升权限的代码
        batch_file.write("@echo off\n")
        batch_file.write(":: 检查管理员权限\n")
        batch_file.write(">nul 2>&1 \"%SYSTEMROOT%\\system32\\cacls.exe\" \"%SYSTEMROOT%\\system32\\config\\system\"\n")
        batch_file.write("if '%errorlevel%' NEQ '0' (\n")
        batch_file.write("    echo 请求管理员权限...\n")
        batch_file.write("    goto UACPrompt\n")
        batch_file.write(") else ( goto gotAdmin )\n")
        batch_file.write(":UACPrompt\n")
        batch_file.write("    echo Set UAC = CreateObject^(\"Shell.Application\"^) > \"%temp%\\getadmin.vbs\"\n")
        batch_file.write("    echo UAC.ShellExecute \"%~s0\", \"\", \"\", \"runas\", 1 >> \"%temp%\\getadmin.vbs\"\n")
        batch_file.write("    \"%temp%\\getadmin.vbs\"\n")
        batch_file.write("    exit /B\n")
        batch_file.write(":gotAdmin\n")
        batch_file.write("    if exist \"%temp%\\getadmin.vbs\" ( del \"%temp%\\getadmin.vbs\" )\n")
        batch_file.write("    pushd \"%CD%\"\n")
        batch_file.write("    CD /D \"%~dp0\"\n")

        # 原有的批处理命令
        batch_file.write("echo Running as administrator...\n")
        for name, path in selected_software:
            if os.path.isfile(path) and path.endswith(".exe"):
                exe_path = path
            else:
                exe_path = find_executable(name, path)
                if isinstance(exe_path, list):
                    exe_path = prompt_user_to_select_executable(name, exe_path)
            if exe_path:
                batch_file.write(f'cd /d "{os.path.dirname(exe_path)}"\n')
                batch_file.write(f'start "" "{exe_path}"\n')
            else:
                messagebox.showwarning("警告", f"在路径 {path} 中未找到可执行文件。")

    messagebox.showinfo("完成", f"批处理文件已生成：{batch_file_path}")


# 提示用户选择可执行文件
def prompt_user_to_select_executable(display_name, exe_files):
    def on_select():
        # 获取用户选择的项目
        selected_index = listbox.curselection()
        if selected_index:
            selected_exe.set(exe_files[selected_index[0]])
            dialog.destroy()
        else:
            messagebox.showwarning("警告", "请先选择一个可执行文件。")

    # 创建一个StringVar来存储选择的可执行文件
    selected_exe = tk.StringVar()
    # 创建一个新的顶级窗口
    dialog = tk.Toplevel()
    dialog.title(f"选择 {display_name} 的可执行文件")
    dialog.geometry("500x400")

    # 创建一个带内边距的Frame
    frame = ttk.Frame(dialog, padding="10")
    frame.pack(fill=tk.BOTH, expand=True)

    # 创建一个滚动条
    scrollbar = ttk.Scrollbar(frame)
    scrollbar.pack(side=tk.RIGHT, fill=tk.Y)

    # 创建一个列表框
    listbox = tk.Listbox(frame, selectmode=tk.SINGLE, yscrollcommand=scrollbar.set)
    listbox.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
    scrollbar.config(command=listbox.yview)

    # 将可执行文件添加到列表框
    for exe in exe_files:
        listbox.insert(tk.END, exe)

    # 创建一个选择按钮
    select_button = ttk.Button(dialog, text="选择", command=on_select)
    select_button.pack(pady=10)

    # 等待窗口关闭
    dialog.wait_window(dialog)

    # 返回选择的可执行文件
    return selected_exe.get()
# 创建主界面
def create_main_window(software_list):
    def filter_software_list(keyword):
        filtered_list = [(name, path) for name, path in software_list if keyword.lower() in name.lower()]
        update_treeview(filtered_list)

    def update_treeview(filtered_list):
        tree.delete(*tree.get_children())
        for software in filtered_list:
            tree.insert('', 'end', values=('', software[0], software[1]))

    def on_search():
        keyword = search_var.get()
        filter_software_list(keyword)

    def toggle_check(event):
        item = tree.identify_row(event.y)
        if item:
            current_values = tree.item(item, 'values')
            new_values = ('✓' if current_values[0] != '✓' else '', current_values[1], current_values[2])
            tree.item(item, values=new_values)

    def on_folder_search():
        folder_path = filedialog.askdirectory()
        if folder_path:
            exe_list = search_exe_in_folder(folder_path)
            if exe_list:
                for exe in exe_list:
                    tree.insert('', 0, values=('', exe[0], exe[1]))
                messagebox.showinfo("搜索完成", f"在文件夹中找到 {len(exe_list)} 个可执行文件")
            else:
                messagebox.showinfo("搜索完成", "未找到可执行文件")

    root = tk.Tk()
    root.title("选择已安装软件")
    root.geometry("800x600")

    # 创建一个主框架
    main_frame = ttk.Frame(root)
    main_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

    # 搜索框和按钮
    search_frame = ttk.Frame(main_frame)
    search_frame.pack(fill=tk.X, pady=(0, 10))

    search_var = tk.StringVar()
    search_entry = ttk.Entry(search_frame, textvariable=search_var)
    search_entry.pack(side=tk.LEFT, expand=True, fill=tk.X)

    search_button = ttk.Button(search_frame, text="搜索关键词", command=on_search)
    search_button.pack(side=tk.LEFT, padx=(5, 0))

    folder_search_button = ttk.Button(search_frame, text="搜索文件夹", command=on_folder_search)
    folder_search_button.pack(side=tk.LEFT, padx=(5, 0))

    # Treeview
    tree_frame = ttk.Frame(main_frame)
    tree_frame.pack(fill=tk.BOTH, expand=True)

    tree = ttk.Treeview(tree_frame, columns=('Check', 'Software', 'Path'), show='headings', selectmode='none')
    tree.heading('Check', text='选择')
    tree.heading('Software', text='软件名称')
    tree.heading('Path', text='安装路径')

    tree.column('Check', width=50, stretch=False, anchor='center')
    tree.column('Software', width=250, stretch=False)
    tree.column('Path', width=500, stretch=True)

    tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)

    scrollbar = ttk.Scrollbar(tree_frame, orient=tk.VERTICAL, command=tree.yview)
    scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
    tree.configure(yscrollcommand=scrollbar.set)

    for software in software_list:
        tree.insert('', 'end', values=('', software[0], software[1]))

    tree.bind('<ButtonRelease-1>', toggle_check)

    # 生成批处理文件按钮
    generate_button = ttk.Button(main_frame, text="生成批处理文件", command=lambda: generate_batch_file([tree.item(item)['values'][1:] for item in tree.get_children() if tree.item(item)['values'][0] == '✓']))
    generate_button.pack(pady=10)

    # 显示选中项路径的Entry
    entry_var = tk.StringVar()
    entry = ttk.Entry(main_frame, textvariable=entry_var, state='readonly')
    entry.pack(fill=tk.X, pady=10)

    def on_select(event):
        selected_item = tree.focus()
        if selected_item:
            path = tree.item(selected_item, 'values')[2]
            entry_var.set(path)

    tree.bind("<<TreeviewSelect>>", on_select)

    def copy_to_clipboard(event):
        root.clipboard_clear()
        root.clipboard_append(entry_var.get())

    entry.bind("<Button-1>", copy_to_clipboard)

    root.mainloop()

if __name__ == "__main__":
    desktop_shortcuts = get_desktop_shortcuts()
    installed_software = get_installed_software()
    combined_software_list = desktop_shortcuts + installed_software
    create_main_window(combined_software_list)
