TARGET_CHARACTERS = list("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmonpqrstuvwxyz123456789")
SEP = "  "

name = input("name: ")
delimiter = input("delimiter: ")

print("Please paste this string into a fancy text generator")
print(SEP.join(TARGET_CHARACTERS))

transformed = input("Transformed text: ").split(SEP)

with open(f"{name}.txt", "w", encoding="UTF-8") as f:
    f.write(delimiter + "\n")
    f.write("" + "\n")
    for original_c, replaced_c in zip(TARGET_CHARACTERS, transformed):
        f.write(f"{original_c} {original_c}҉" + "\n")
