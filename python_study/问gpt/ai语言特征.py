import json
from 问gpt import 问gpt as 问gpt
def 提取语言特征(用户回复数据):
    提示词 = '''
    你是一个语言分析专家。请根据提供的用户回复数据,提取该用户的语言特征,并以JSON格式返回。
    分析应包括但不限于:词汇使用、句式结构、表达方式、情感倾向、常用话题等。
    请确保分析全面且详细,以便于之后能够精确模仿该用户的语言风格。
    '''
    
    问题 = f"{提示词}\n\n用户回复数据:\n{用户回复数据}"
    
    回答 = 问gpt(问题)
    
    try:
        特征 = json.loads(回答)
        return 特征
    except json.JSONDecodeError:
        print("GPT返回的不是有效的JSON格式,请检查回答内容")
        return None
def 模仿用户语言(特征, 话题):
    '''
    :param 特征:
    :param 话题:
    :return: 根据问gpt和用户特征来回答话题
    '''
    提示词 = f'''
    你是一个语言模仿专家。请根据提供的用户语言特征,模仿该用户的语言风格来讨论给定的话题。
    用户语言特征:
    {json.dumps(特征, ensure_ascii=False, indent=2)}
    请严格按照这些特征来生成回复,确保模仿的准确性和真实性。
    我要回复另一个人信息，这是他给我发的，请帮我回复,回复一次
    '''# 我是lanendiencgr，我要回复另一个人信息，请帮我回复
    问题 = f"{提示词}\n\n请讨论以下话题: {话题}"
    回答 = 问gpt(问题)
    return 回答
def 示例特征():
    return {
        "language_profile": {  # 语言特征
            "syntax": {  # 句法
                "sentence_length": {  # 句子长度
                    "average": 10,  # 平均长度
                    "distribution": {  # 长度分布
                        "short": 70,  # 短句子比例
                        "medium": 20,  # 中等长度句子比例
                        "long": 10  # 长句子比例
                    }
                },
                "sentence_structure": {  # 句子结构
                    "simple_sentences": 60,  # 简单句比例
                    "compound_sentences": 30,  # 并列句比例
                    "complex_sentences": 10  # 复合句比例
                },
                "use_of_clauses": {  # 从句使用
                    "relative_clauses": 3,  # 关系从句比例
                    "adverbial_clauses": 5,  # 状语从句比例
                    "noun_clauses": 2  # 名词性从句比例
                },
                "punctuation_usage": {  # 标点符号使用
                    "commas": 15,  # 逗号使用频率
                    "periods": 25,  # 句号使用频率
                    "exclamations": 3,  # 感叹号使用频率
                    "questions": 7,  # 问号使用频率
                    "semicolons": 0,  # 分号使用频率
                    "ellipses": 2  # 省略号使用频率
                }
            },
            "vocabulary": {  # 词汇
                "lexical_richness": {  # 词汇丰富度
                    "unique_words_percentage": 70,  # 独特词汇比例
                    "common_words_percentage": 30  # 常用词汇比例
                },
                "preferred_words": [  # 偏好词汇
                    "账号",  # 账号
                    "适配",  # 适配
                    "问题",  # 问题
                    "服务器"  # 服务器
                ],
                "slang_and_informalities": {  # 俚语和非正式用语
                    "frequency": 10,  # 使用频率
                    "examples": [  # 示例
                        "搞钱",  # 搞钱
                        "陪葬"  # 陪葬
                    ]
                },
                "technical_jargon": {  # 技术术语
                    "frequency": 15,  # 使用频率
                    "examples": [  # 示例
                        "服务器",  # 服务器
                        "适配",  # 适配
                        "LINUX"  # LINUX
                    ]
                }
            },
            "expression_style": {  # 表达风格
                "emotional_tone": {  # 情感基调
                    "positive": 10,  # 积极情感比例
                    "negative": 20,  # 消极情感比例
                    "neutral": 70  # 中性情感比例
                },
                "humor_usage": {  # 幽默使用
                    "frequency": 5,  # 使用频率
                    "types": ["sarcasm"]  # 幽默类型
                },
                "politeness_level": {  # 礼貌程度
                    "formal": 20,  # 正式
                    "informal": 80  # 非正式
                },
                "directness": {  # 直接性
                    "direct": 70,  # 直接
                    "indirect": 30  # 间接
                }
            },
            "recurrent_phrases": {  # 反复出现的短语
                "phrases": [  # 短语
                    "不认同",  # 不认同
                    "回头看看",  # 回头看看
                    "搞钱"  # 搞钱
                ],
                "frequency": 10  # 使用频率
            },
            "discussion_behavior": {  # 讨论行为
                "agreement_tendency": {  # 同意倾向
                    "agree": 20,  # 同意
                    "disagree": 80  # 不同意
                },
                "response_style": {  # 回复风格
                    "concise": 70,  # 简洁
                    "detailed": 30  # 详细
                },
                "question_asking": {  # 提问
                    "frequency": 15,  # 提问频率
                    "types": ["rhetorical", "clarification"]  # 提问类型
                }
            },
            "temporal_pattern": {  # 时间模式
                "posting_time": {  # 发帖时间
                    "morning": 20,  # 早上
                    "afternoon": 40,  # 下午
                    "evening": 30,  # 晚上
                    "night": 10  # 夜间
                },
                "response_time": {  # 回复时间
                    "fast": 70,  # 快速
                    "moderate": 20,  # 适中
                    "slow": 10  # 缓慢
                }
            }
        }
    }
def 可爱温柔特征():
    return {
        "language_profile": {
            "syntax": {
                "sentence_length": {
                    "average": 8,
                    "distribution": {
                        "short": 60,
                        "medium": 35,
                        "long": 5
                    }
                },
                "sentence_structure": {
                    "simple_sentences": 70,
                    "compound_sentences": 25,
                    "complex_sentences": 5
                },
                "use_of_clauses": {
                    "relative_clauses": 2,
                    "adverbial_clauses": 3,
                    "noun_clauses": 1
                },
                "punctuation_usage": {
                    "commas": 10,
                    "periods": 20,
                    "exclamations": 15,
                    "questions": 10,
                    "semicolons": 0,
                    "ellipses": 5
                }
            },
            "vocabulary": {
                "lexical_richness": {
                    "unique_words_percentage": 60,
                    "common_words_percentage": 40
                },
                "preferred_words": [
                    "可爱",
                    "温柔",
                    "喜欢",
                    "开心"
                ],
                "slang_and_informalities": {
                    "frequency": 15,
                    "examples": [
                        "萌萌哒",
                        "软软的"
                    ]
                },
                "technical_jargon": {
                    "frequency": 5,
                    "examples": [
                        "爱心",
                        "温暖"
                    ]
                }
            },
            "expression_style": {
                "emotional_tone": {
                    "positive": 70,
                    "negative": 5,
                    "neutral": 25
                },
                "humor_usage": {
                    "frequency": 20,
                    "types": ["轻松", "俏皮"]
                },
                "politeness_level": {
                    "formal": 10,
                    "informal": 90
                },
                "directness": {
                    "direct": 40,
                    "indirect": 60
                }
            },
            "recurrent_phrases": {
                "phrases": [
                    "真的很喜欢",
                    "好开心呀",
                    "超级可爱"
                ],
                "frequency": 25
            },
            "discussion_behavior": {
                "agreement_tendency": {
                    "agree": 80,
                    "disagree": 20
                },
                "response_style": {
                    "concise": 40,
                    "detailed": 60
                },
                "question_asking": {
                    "frequency": 25,
                    "types": ["关心", "好奇"]
                }
            },
            "temporal_pattern": {
                "posting_time": {
                    "morning": 30,
                    "afternoon": 30,
                    "evening": 35,
                    "night": 5
                },
                "response_time": {
                    "fast": 80,
                    "moderate": 15,
                    "slow": 5
                }
            }
        }
    }
if __name__ == '__main__':
    # 示例使用
    # 用户回复数据 = "这里放入用户的实际回复数据"
    # 特征 = 提取语言特征(用户回复数据)
    # if 特征:
    #     print("提取到的语言特征:")
    #     print(json.dumps(特征, ensure_ascii=False, indent=2))
    #     话题 = "对最近的一个热门话题发表看法"
    #     模仿回复 = 模仿用户语言(特征, 话题)
    #     print(f"\n模仿用户讨论'{话题}'的回复:")
    #     print(模仿回复)
    # else:
    #     print("无法提取语言特征,请检查输入数据或GPT响应")
    #
    # 使用示例特征
    示例特征数据 = 可爱温柔特征() # 示例特征()
    示例话题 = '''
123......:
确实通过锻炼可以增肌就看起来比较强壮了

123......:
不好玩，本来想写网络性侵未成年犯罪的成因与预防，老师说题目有疑问不行，准备换成大学生恶性犯罪的成因及防控

123......:
啊怎么天天做

123......:
你是不是气血不足啊听说气血不足也会经常做噩梦

'''
    示例模仿回复 = 模仿用户语言(示例特征数据, 示例话题)
    print(f"\n使用示例特征模仿用户讨论'{示例话题}'的回复:")
    print(示例模仿回复)

