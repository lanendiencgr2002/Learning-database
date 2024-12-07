def is_english_text(text, english_threshold=0.5):
    """
    检查文本是否需要翻译（英译中）
    
    Args:
        text: 输入文本
        english_threshold: 英文字符占比阈值，默认0.5（50%）
    
    Returns:
        bool: True表示需要翻译（英文占主导），False表示不需要翻译（中文占主导）
    """
    if not text or len(text.strip()) == 0:
        return False
    
    # 统计英文字母
    english_chars = len([c for c in text if c.isalpha() and ord(c) < 128])
    
    # 统计中文字符
    chinese_chars = len([c for c in text if '\u4e00' <= c <= '\u9fff'])
    
    # 计算总有效字符数
    total_valid_chars = english_chars + chinese_chars
    
    if total_valid_chars == 0:
        return False
    
    # 计算英文字符占比
    english_ratio = english_chars / total_valid_chars
    
    # 返回是否需要翻译（英文占比高于阈值时需要翻译）
    return english_ratio >= english_threshold


if __name__ == "__main__":
    test_texts = [
        "This is pure English text",  # 需要翻译
        "这是纯中文文本",            # 不需要翻译
        "This is mixed 中文 content", # 可能需要翻译，��决于英文占比
        "这是混合的English文本",      # 可能不需要翻译，取决于英文占比
        "Hello 世界"                 # 可能需要翻译，取决于英文占比
    ]
    
    for text in test_texts:
        need_translation = is_english_text(text)
        print(f"\n文本: {text}")
        print(f"需要翻译: {need_translation}")
