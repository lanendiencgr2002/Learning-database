# Python-docx 使用教程

## 一、安装配置

### 安装
```python
pip install python-docx
```

### 基本导入
```python
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.style import WD_STYLE_TYPE
```

## 二、基础操作大全

### 1. 创建和打开文档
```python
# 创建新文档
doc = Document()

# 打开已有文档
doc = Document('example.docx')

# 保存文档
doc.save('new_document.docx')
```

### 2. 添加段落和格式化
```python
def create_formatted_doc():
    doc = Document()
    
    # 添加标题
    doc.add_heading('项目周报', level=0)
    
    # 添加段落
    p = doc.add_paragraph('本周工作进展：')
    p.add_run('重点项目已完成').bold = True  # 加粗
    p.add_run('，现进入测试阶段。')
    
    # 设置段落格式
    paragraph = doc.add_paragraph()
    paragraph.alignment = WD_ALIGN_PARAGRAPH.CENTER  # 居中对齐
    run = paragraph.add_run('项目进度：80%')
    run.font.size = Pt(14)  # 设置字号
    run.font.name = '微软雅黑'  # 设置字体
    
    return doc
```

### 3. 表格处理
```python
def create_project_table(doc, data):
    # 添加表格
    table = doc.add_table(rows=1, cols=4)
    table.style = 'Table Grid'
    
    # 设置表头
    header_cells = table.rows[0].cells
    headers = ['项目名称', '负责人', '完成度', '备注']
    for i, header in enumerate(headers):
        header_cells[i].text = header
        
    # 添加数据行
    for item in data:
        row_cells = table.add_row().cells
        for i, value in enumerate(item):
            row_cells[i].text = str(value)
            
    return doc
```

## 三、办公提效小技巧

### 1. 批量生成周报模板
```python
def generate_weekly_reports():
    team_members = ['张三', '李四', '王五']
    
    for member in team_members:
        doc = Document()
        
        # 添加标题
        doc.add_heading(f'{member}周报', level=0)
        
        # 添加固定部分
        sections = [
            '本周工作内容：',
            '工作成果：',
            '存在问题：',
            '下周计划：'
        ]
        
        for section in sections:
            p = doc.add_paragraph()
            p.add_run(section).bold = True
            doc.add_paragraph('• ')  # 添加项目符号
            
        # 保存文件
        doc.save(f'{member}周报_{time.strftime("%Y%m%d")}.docx')
```

### 2. 自动提取文档内容
```python
def extract_doc_content(file_path):
    doc = Document(file_path)
    content = []
    
    for paragraph in doc.paragraphs:
        if paragraph.text.strip():  # 忽略空段落
            content.append(paragraph.text)
            
    return '\n'.join(content)
```

### 3. 格式刷子
```python
def copy_format(source_run, target_run):
    """复制格式属性"""
    target_run.bold = source_run.bold
    target_run.italic = source_run.italic
    target_run.underline = source_run.underline
    target_run.font.size = source_run.font.size
    target_run.font.name = source_run.font.name
```

## 四、高级实战：智能周报生成器

这是一个高效的周报生成工具，可以将周报编写时间从1小时缩短到1分钟！

```python
class SmartReportGenerator:
    def __init__(self):
        self.doc = Document()
        self.configure_styles()
    
    def configure_styles(self):
        """配置文档样式"""
        style = self.doc.styles['Normal']
        font = style.font
        font.name = '微软雅黑'
        font.size = Pt(10.5)
    
    # ... 其他方法实现 ...
```

## 实用小技巧

### 使用模板
```python
def load_template(template_path):
    """加载文档模板"""
    if not os.path.exists(template_path):
        raise FileNotFoundError('模板文件不存在')
    return Document(template_path)
```

### 段落样式复制
```python
def copy_paragraph_format(source_paragraph, target_paragraph):
    """复制段落格式"""
    target_paragraph.style = source_paragraph.style
    target_paragraph.paragraph_format.alignment = source_paragraph.paragraph_format.alignment
    target_paragraph.paragraph_format.line_spacing = source_paragraph.paragraph_format.line_spacing
```

### 批量替换文本
```python
def batch_replace_text(doc, replacements):
    """批量替换文档中的文本"""
    for paragraph in doc.paragraphs:
        for old_text, new_text in replacements.items():
            if old_text in paragraph.text:
                paragraph.text = paragraph.text.replace(old_text, new_text)
```

## 使用建议

- 创建自己的文档模板库
- 建立常用格式配置文件
- 使用参数化配置
- 定期备份重要文档
- 处理大文档时注意内存使用

