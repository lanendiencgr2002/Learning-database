import os

def extract_english_name(text):
    """提取括号中的英文名称"""
    if '(' in text and ')' in text:
        start = text.find('(') + 1
        end = text.find(')')
        return text[start:end]
    return ""

def main():
    当前目录=os.path.dirname(os.path.abspath(__file__))
    # 读取文件
    with open(os.path.join(当前目录,'公司名.txt'), 'r', encoding='utf-8') as f:
        hotels = f.readlines()
    
    # 提取英文名并写入新文件
    with open(os.path.join(当前目录,'酒店英文名.txt'), 'w', encoding='utf-8') as f:
        for hotel in hotels:
            hotel = hotel.strip()
            eng_name = extract_english_name(hotel)
            if eng_name:
                # 只保留包含英文字母的名称
                if any(c.isalpha() for c in eng_name):
                    f.write(f"{eng_name}\n")

if __name__ == "__main__":
    main() 