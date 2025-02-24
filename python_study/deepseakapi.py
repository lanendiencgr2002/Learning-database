# 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

from openai import OpenAI

client = OpenAI(
  base_url = "https://integrate.api.nvidia.com/v1",
  api_key = "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"
)

completion = client.chat.completions.create(
  model="deepseek-ai/deepseek-r1",
  messages=[{"role":"user","content":"额"}],
  temperature=0.6,
  top_p=0.7,
  max_tokens=4096,
  stream=True 
)

for chunk in completion:
  if chunk.choices[0].delta.content is not None:
    print(chunk.choices[0].delta.content, end="")

